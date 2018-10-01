package com.camp.sparkservice.domain;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.GBTClassifier;
import org.apache.spark.ml.feature.*;
import static org.apache.spark.sql.functions.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camp.sparkservice.service.SparkService;

public class ChurnModelBuildProcess extends SparkProcess {

	private Logger logger = LoggerFactory.getLogger(ChurnModelBuildProcess.class);

	private ChurnModelBuildRequest request;

	public ChurnModelBuildProcess(SparkService sparkService, ChurnModelBuildRequest request) {
		super(sparkService);
		this.request = request;
	}

	/*
	 * 1. Load data from UserEvent table for given application 2. If data fits in
	 * memory, process it, otherwise save it to temp file in disk 3. Load data into
	 * spark 4. Find top events without sing-in event 5. Find unique users 6.
	 * Calculate counts for events for each user 7. Decide which user is out of app
	 * (inactive more than x days) 8. Generate GBT or other tree based algorithm 9.
	 * Test model (AUROC, matrix) 10 Save model if ok otherwise inform about lack of
	 * data
	 */
	@Override
	public void execute() {
		this.setStatus(SparkProcessStatus.PROCESSING);
		logger.info("Start processing!");
		long eventCount = countEvents();
		logger.info("Events found for application: {}", eventCount);
		List<UserEvent> events = selectUserEvents(0, 100);
		logger.info("Events: {}", events.size());
		JavaRDD<UserEvent> eventsRDD = getSparkService().getSparkContext().parallelize(events);
		Dataset<Row> eventsDF = sparkSession().createDataFrame(eventsRDD, UserEvent.class);

		logger.info("**** Events: {}", eventsDF.count());
		eventsDF.select("category").show();

		Dataset<Row> registerEventsDF = eventsDF
				.where(eventsDF.col("category").equalTo(request.getRegisterEventCategory()));
		logger.info("**** Register events: {}", registerEventsDF.count());
		Dataset<Row> uniqueUsersDF = registerEventsDF.groupBy("userId").count();
		uniqueUsersDF = uniqueUsersDF.withColumnRenamed("userId", "userId2");
		logger.info("**** Unique users: {}", uniqueUsersDF.count());
		Dataset<Row> uniqueUsersRegisteredDF = registerEventsDF.join(uniqueUsersDF,
				registerEventsDF.col("userId").equalTo(uniqueUsersDF.col("userId2")));

		uniqueUsersRegisteredDF = uniqueUsersRegisteredDF.select(uniqueUsersRegisteredDF.col("userId"),
				uniqueUsersRegisteredDF.col("createdAt"));
		uniqueUsersRegisteredDF.show();

		Dataset<Row> uniqueEventCategories = eventsDF.groupBy("category").count().select("category");
		List<Row> categoriesAsRowsList = uniqueEventCategories.collectAsList();
		List<String> categories = new ArrayList<String>();
		for (Row categoryRow : categoriesAsRowsList) {
			categories.add(categoryRow.mkString());
		}

		Dataset<Row> eventsCountDF = uniqueUsersDF;
		for (String category : categories) {
			logger.info("**** CATEGORY: {}", category);
			Dataset<Row> categoryDF = eventsDF.select("userId", "category");
			categoryDF = categoryDF.where(categoryDF.col("category").equalTo(category)).groupBy("userId").count();
			categoryDF = categoryDF.withColumnRenamed("count", category.toLowerCase() + "_event_count");
			categoryDF = categoryDF.withColumnRenamed("userId", "userId_" + category);
			eventsCountDF = eventsCountDF.join(categoryDF,
					eventsCountDF.col("userId2").equalTo(categoryDF.col("userId_" + category)));
		}
		eventsCountDF = eventsCountDF.withColumnRenamed("userId2", "userId");
		eventsCountDF = eventsCountDF.drop("count");
		for (String category : categories) {
			eventsCountDF = eventsCountDF.drop(eventsCountDF.col("userId_" + category));
		}
		eventsCountDF.show();

		Dataset<Row> eventsWithChangedDateToTimestamp = eventsDF.withColumn("createdAtTimestamp",
				eventsDF.col("createdAt").cast("timestamp"));

		Dataset<Row> lastEventDF = eventsWithChangedDateToTimestamp.groupBy("userId")
				.agg(org.apache.spark.sql.functions.max("createdAtTimestamp").as("maxEventTime"));
		Dataset<Row> firstEventDF = eventsWithChangedDateToTimestamp.groupBy("userId")
				.agg(org.apache.spark.sql.functions.min("createdAtTimestamp").as("minEventTime"));

		lastEventDF.show();
		firstEventDF.show();

		Dataset<Row> lastAndFirstEventDF = lastEventDF.join(firstEventDF, "userId");
		lastAndFirstEventDF = lastAndFirstEventDF.withColumn("diff", lastAndFirstEventDF.col("maxEventTime")
				.cast("long").minus(lastAndFirstEventDF.col("minEventTime").cast("long")));
		lastAndFirstEventDF = lastAndFirstEventDF.select("userId", "diff");
		lastAndFirstEventDF.show();

		Dataset<Row> usersDataDF = eventsCountDF.join(lastAndFirstEventDF, "userId");
		usersDataDF = usersDataDF.withColumn("label", when(usersDataDF.col("diff").geq(604800),1).otherwise(0));
		usersDataDF = usersDataDF.drop("userId");
		usersDataDF.show();
		
		String cols[] = usersDataDF.drop("label").columns();
		
		VectorAssembler vectorAssembler = new VectorAssembler().setInputCols(cols).setOutputCol("features");
		Dataset<Row> usersDataAssembledAsVector = vectorAssembler.transform(usersDataDF).select("label","features");
		usersDataAssembledAsVector.show();
		
		
		
		// Index labels, adding metadata to the label column.
		// Fit on whole dataset to include all labels in index.
		StringIndexerModel labelIndexer = new StringIndexer()
		  .setInputCol("label")
		  .setOutputCol("indexedLabel")
		  .fit(usersDataAssembledAsVector);
		// Automatically identify categorical features, and index them.
		// Set maxCategories so features with > 4 distinct values are treated as continuous.
		VectorIndexerModel featureIndexer = new VectorIndexer()
		  .setInputCol("features")
		  .setOutputCol("indexedFeatures")
		  .setMaxCategories(4)
		  .fit(usersDataAssembledAsVector);

		// Split the data into training and test sets (30% held out for testing)
		Dataset<Row>[] splits = usersDataAssembledAsVector.randomSplit(new double[] {0.7, 0.3});
		Dataset<Row> trainingData = splits[0];
		Dataset<Row> testData = splits[1];


		// Train a GBT model.
		GBTClassifier gbt = new GBTClassifier()
		  .setLabelCol("indexedLabel")
		  .setFeaturesCol("indexedFeatures")
		  .setMaxIter(10);

		// Convert indexed labels back to original labels.
		IndexToString labelConverter = new IndexToString()
		  .setInputCol("prediction")
		  .setOutputCol("predictedLabel")
		  .setLabels(labelIndexer.labels());

		// Chain indexers and GBT in a Pipeline.
		Pipeline pipeline = new Pipeline()
		  .setStages(new PipelineStage[] {labelIndexer, featureIndexer, gbt, labelConverter});

		// Train model. This also runs the indexers.
		PipelineModel model = pipeline.fit(trainingData);

		// Make predictions.
		Dataset<Row> predictions = model.transform(testData);

		// Select example rows to display.
		predictions.select("predictedLabel", "label", "features").show(5);

		this.setStatus(SparkProcessStatus.FINISHED);
		logger.info("Finished processing!");
	}

	private long countEvents() {
		return getSparkService().getUserEventService()
				.countUserEvent(request.getApplicationId(), request.getStartDate(), request.getEndDate()).getCount();
	}

	private List<UserEvent> selectUserEvents(int page, int size) {
		logger.info("Selecting events startDate: {}, endDate: {} page: {}, size: {}", request.getStartDate(),
				request.getEndDate(), page, size);
		return getSparkService().getUserEventService().selectUserEvents(request.getApplicationId(),
				request.getStartDate(), request.getEndDate(), page, size);
	}
}
