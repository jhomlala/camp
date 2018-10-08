package com.camp.sparkservice.domain;

import static org.apache.spark.sql.functions.when;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.GBTClassifier;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.feature.VectorIndexer;
import org.apache.spark.ml.feature.VectorIndexerModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camp.sparkservice.service.SparkService;

public class ChurnModelBuildProcess extends SparkProcess {

	private static final long serialVersionUID = 1990293965273645689L;
	private static final int pageSize = 100;

	private Logger logger = LoggerFactory.getLogger(ChurnModelBuildProcess.class);

	private ChurnModelBuildRequest request;

	public ChurnModelBuildProcess(SparkService sparkService, ChurnModelBuildRequest request) {
		super(sparkService);
		this.request = request;

	}

	@Override
	public void execute() {
		try {
			this.setStatus(SparkProcessStatus.PROCESSING);
			logger.info("Started churn mogel build process");

			long eventCount = countEvents();
			logger.info("Count of events, found for build process: {}", eventCount);
			List<UserEvent> events = loadUserEvents(eventCount);

			logger.info("Count of events downloaded from database for build process: {}", events.size());
			JavaRDD<UserEvent> eventsRDD = getSparkService().getSparkContext().parallelize(events);
			Dataset<Row> eventsDF = sparkSession().createDataFrame(eventsRDD, UserEvent.class);

			Dataset<Row> registerEventsDF = eventsDF
					.where(eventsDF.col("category").equalTo(request.getRegisterEventCategory()));
			logger.info("Count of register events: {}", registerEventsDF.count());

			Dataset<Row> uniqueUsersDF = registerEventsDF.groupBy("userId").count();
			uniqueUsersDF = uniqueUsersDF.withColumnRenamed("userId", "userId2");

			logger.info("Count of unique users:", uniqueUsersDF.count());
			Dataset<Row> uniqueUsersRegisteredDF = registerEventsDF.join(uniqueUsersDF,
					registerEventsDF.col("userId").equalTo(uniqueUsersDF.col("userId2")));

			uniqueUsersRegisteredDF = uniqueUsersRegisteredDF.select(uniqueUsersRegisteredDF.col("userId"),
					uniqueUsersRegisteredDF.col("createdAt"));
			uniqueUsersRegisteredDF.show();

			List<String> categories = selectUniqueEventCategories(eventsDF);
			Dataset<Row> eventsCountDF = countUniqueEvents(eventsDF, uniqueUsersDF, categories);

			Dataset<Row> diffBetweenLastAndFirstEventDF = calculateDiffBetweenFirstAndLastEvent(eventsDF);
			Dataset<Row> usersDataDF = setupTrainingDF(eventsCountDF, diffBetweenLastAndFirstEventDF);
			String[] cols = usersDataDF.drop("label").columns();

			VectorAssembler vectorAssembler = new VectorAssembler().setInputCols(cols).setOutputCol("features");
			Dataset<Row> usersDataAssembledAsVector = vectorAssembler.transform(usersDataDF).select("label",
					"features");

			StringIndexerModel labelIndexer = setupLabelIndexer(usersDataAssembledAsVector);
			VectorIndexerModel featureIndexer = setupFeaturesIndexer(usersDataAssembledAsVector);
			Dataset<Row>[] splits = usersDataAssembledAsVector.randomSplit(new double[] { 0.7, 0.3 });
			Dataset<Row> trainingData = splits[0];
			Dataset<Row> testData = splits[1];

			PipelineModel model = trainModel(labelIndexer, featureIndexer, trainingData);
			Dataset<Row> predictions = model.transform(testData);
			predictions.select("predictedLabel", "label", "features").show(5);

			saveModel(model);
			saveCategories(categories);

			this.setStatus(SparkProcessStatus.FINISHED);
			getSparkService().onSparkProcessCompleted(this);
			logger.info("Finished processing!");
		} catch (Exception exc) {
			logger.error("Exception in process method: {}", ExceptionUtils.getFullStackTrace(exc));
			this.setStatus(SparkProcessStatus.FINISHED);
			this.error = exc.getMessage();
			getSparkService().onSparkProcessCompleted(this);
		}
	}

	private void saveCategories(List<String> categories) {
		try {
			String filePath = getSparkService().getConfig().getSparkModelsDir() + request.getApplicationId()
					+ "/categories.json";
			String content = getSparkService().getGson().toJson(categories);
			Files.write(Paths.get(filePath), content.getBytes());
			logger.info("Saved categories to files");
		} catch (Exception exc) {
			this.error = exc.getMessage();
			logger.error("Exception in saveCategories. Parameter: {}, exc: {}", categories,
					ExceptionUtils.getFullStackTrace(exc));
		}
	}

	private void saveModel(PipelineModel model) {
		try {
			logger.info("Saving model");
			String rootPath = getSparkService().getConfig().getSparkModelsDir() + request.getApplicationId();
			File dir = new File(rootPath);
			if (dir.exists()) {
				deleteDirectoryStream(Paths.get(rootPath));
				dir.delete();
			}
			model.save(rootPath);
			this.result = rootPath;
			logger.info("Model saved");
		} catch (Exception exc) {
			this.error = exc.getMessage();
			logger.error("Exception in saveModel. Parameter: {}, exc: {}", model,
					ExceptionUtils.getFullStackTrace(exc));
		}
	}

	private void deleteDirectoryStream(Path path) {
		try {
			Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (Exception exc) {
			this.error = exc.getMessage();
			exc.printStackTrace();
		}
	}

	private PipelineModel trainModel(StringIndexerModel labelIndexer, VectorIndexerModel featureIndexer,
			Dataset<Row> trainingData) {
		GBTClassifier gbt = new GBTClassifier().setLabelCol("indexedLabel").setFeaturesCol("indexedFeatures")
				.setMaxIter(10);

		IndexToString labelConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel")
				.setLabels(labelIndexer.labels());

		Pipeline pipeline = new Pipeline()
				.setStages(new PipelineStage[] { labelIndexer, featureIndexer, gbt, labelConverter });

		logger.info("Training data: ");
		trainingData.show();

		PipelineModel model = pipeline.fit(trainingData);
		return model;
	}

	private VectorIndexerModel setupFeaturesIndexer(Dataset<Row> usersDataAssembledAsVector) {
		return new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").setMaxCategories(4)
				.fit(usersDataAssembledAsVector);
	}

	private StringIndexerModel setupLabelIndexer(Dataset<Row> usersDataAssembledAsVector) {
		return new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(usersDataAssembledAsVector);

	}

	private Dataset<Row> setupTrainingDF(Dataset<Row> eventsCountDF, Dataset<Row> diffBetweenLastAndFirstEventDF) {
		Dataset<Row> usersDataDF = eventsCountDF.join(diffBetweenLastAndFirstEventDF, "userId");
		usersDataDF = usersDataDF.withColumn("label", when(usersDataDF.col("diff").geq(604800), 1).otherwise(0));
		usersDataDF = usersDataDF.drop("userId");
		return usersDataDF;
	}

	private Dataset<Row> calculateDiffBetweenFirstAndLastEvent(Dataset<Row> eventsDF) {
		Dataset<Row> eventsWithChangedDateToTimestamp = eventsDF.withColumn("createdAtTimestamp",
				eventsDF.col("createdAt").cast("timestamp"));

		Dataset<Row> lastEventDF = eventsWithChangedDateToTimestamp.groupBy("userId")
				.agg(org.apache.spark.sql.functions.max("createdAtTimestamp").as("maxEventTime"));
		Dataset<Row> firstEventDF = eventsWithChangedDateToTimestamp.groupBy("userId")
				.agg(org.apache.spark.sql.functions.min("createdAtTimestamp").as("minEventTime"));

		Dataset<Row> lastAndFirstEventDF = lastEventDF.join(firstEventDF, "userId");
		lastAndFirstEventDF = lastAndFirstEventDF.withColumn("diff", lastAndFirstEventDF.col("maxEventTime")
				.cast("long").minus(lastAndFirstEventDF.col("minEventTime").cast("long")));
		lastAndFirstEventDF = lastAndFirstEventDF.select("userId", "diff");
		return lastAndFirstEventDF;
	}

	private Dataset<Row> countUniqueEvents(Dataset<Row> eventsDF, Dataset<Row> uniqueUsersDF, List<String> categories) {
		Dataset<Row> eventsCountDF = uniqueUsersDF.select("userId2", "count");
		for (String category : categories) {
			logger.info("Processing category: {}", category);
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
		return eventsCountDF;
	}

	private List<String> selectUniqueEventCategories(Dataset<Row> eventsDF) {
		Dataset<Row> uniqueEventCategories = eventsDF.groupBy("category").count().select("category");
		List<Row> categoriesAsRowsList = uniqueEventCategories.collectAsList();
		List<String> categories = new ArrayList<>();
		for (Row categoryRow : categoriesAsRowsList) {
			categories.add(categoryRow.mkString());
		}
		return categories;
	}

	public List<UserEvent> loadUserEvents(long count) {
		List<UserEvent> userEvents = new ArrayList<>();
		int selectIterations = (int) Math.ceil(count / (double) pageSize);
		for (int iteration = 0; iteration <= selectIterations; iteration++) {
			userEvents.addAll(selectUserEvents(selectIterations, pageSize));
		}
		return userEvents;
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
