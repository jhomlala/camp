package com.camp.sparkservice.domain;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camp.sparkservice.service.SparkService;
import com.google.common.reflect.TypeToken;

public class ChurnPredictionProcess extends SparkProcess {
	private static final int pageSize = 100;
	private static final long serialVersionUID = -2114214764831644182L;
	private Logger logger = LoggerFactory.getLogger(ChurnModelBuildProcess.class);

	private ChurnPredictRequest request;
	private Dataset<Row> predictions;

	public ChurnPredictionProcess(SparkService sparkService, ChurnPredictRequest request) {
		super(sparkService);
		this.request = request;
	}

	@Override
	public void execute() {
		try {
			this.setStatus(SparkProcessStatus.PROCESSING);
			logger.info("Started churn mogel build process");

			long eventCount = countEvents();
			logger.info("Count of events, found for predict process: {}", eventCount);
			List<UserEvent> events = loadUserEvents(eventCount);

			logger.info("Count of events downloaded from database for build process: {}", events.size());
			JavaRDD<UserEvent> eventsRDD = getSparkService().getSparkContext().parallelize(events);
			Dataset<Row> eventsDF = sparkSession().createDataFrame(eventsRDD, UserEvent.class);

			List<String> categories = selectModelCategories();
			Dataset<Row> eventsCountDF = countUniqueEvents(eventsDF, categories);
			Dataset<Row> diffBetweenLastAndFirstEventDF = calculateDiffBetweenFirstAndLastEvent(eventsDF);
			Dataset<Row> usersDataDF = setupTrainingDF(eventsCountDF, diffBetweenLastAndFirstEventDF);

			String[] cols = usersDataDF.drop("label").columns();
			VectorAssembler vectorAssembler = new VectorAssembler().setInputCols(cols).setOutputCol("features");
			Dataset<Row> usersDataAssembledAsVector = vectorAssembler.transform(usersDataDF).select("features");
			usersDataAssembledAsVector.show();

			PipelineModel model = PipelineModel
					.load(getSparkService().getConfig().getSparkModelsDir() + request.getApplicationId());
			predictions = model.transform(usersDataAssembledAsVector);

			logger.info("Predictions:");
			predictions.show();

			this.result = predictions.select("predictedLabel").map(row -> row.mkString(), Encoders.STRING())
					.collectAsList().toString();
			this.setStatus(SparkProcessStatus.FINISHED);
			logger.info("Finished processing!");
			getSparkService().onSparkProcessCompleted(this);
		} catch (Exception exc) {
			logger.error("Exception in process method: {}", ExceptionUtils.getFullStackTrace(exc));
			this.setStatus(SparkProcessStatus.FINISHED);
			this.error = exc.getMessage();
			getSparkService().onSparkProcessCompleted(this);
		}
	}

	private List<String> selectModelCategories() {
		try {
			String categoriesJson = new String(
					Files.readAllBytes(Paths.get(getSparkService().getConfig().getSparkModelsDir()
							+ request.getApplicationId() + "/categories.json")));
			return getSparkService().getGson().fromJson(categoriesJson, new TypeToken<ArrayList<String>>() {
				private static final long serialVersionUID = 1L;
			}.getType());

		} catch (Exception exc) {
			error = exc.getMessage();
			return Collections.emptyList();
		}
	}

	private Dataset<Row> setupTrainingDF(Dataset<Row> eventsCountDF, Dataset<Row> diffBetweenLastAndFirstEventDF) {
		Dataset<Row> usersDataDF = eventsCountDF.join(diffBetweenLastAndFirstEventDF, "userId");
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

	private Dataset<Row> countUniqueEvents(Dataset<Row> eventsDF, List<String> categories) {
		logger.info("Categories: {}", categories);
		Dataset<Row> eventsCountDF = eventsDF.groupBy("userId").count().withColumnRenamed("userId", "userId2");
		Dataset<Row> mockDF = eventsCountDF.select("userId2");

		for (String category : categories) {
			logger.info("Processing category: {}", category);
			Dataset<Row> categoryDF = eventsDF.select("userId", "category");
			categoryDF = categoryDF.where(categoryDF.col("category").equalTo(category)).groupBy("userId").count();
			categoryDF = categoryDF.withColumnRenamed("count", category.toLowerCase() + "_event_count");
			categoryDF = categoryDF.withColumnRenamed("userId", "userId_" + category);
			if (categoryDF.count() == 0) {
				Dataset<Row> mockCategoryDF = mockDF.withColumnRenamed("userId2", "userId_" + category);
				mockCategoryDF = mockCategoryDF.withColumn(category.toLowerCase() + "_event_count", functions.lit(0));
				categoryDF = mockCategoryDF;
			}
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

	public List<UserEvent> loadUserEvents(long count) {
		List<UserEvent> userEvents = new ArrayList<>();
		int selectIterations = (int) Math.ceil(count / (double) pageSize);
		for (int iteration = 0; iteration <= selectIterations; iteration++) {
			userEvents.addAll(selectUserEvents(selectIterations, pageSize));
		}
		return userEvents;
	}

	private long countEvents() {
		return getSparkService().getUserEventService().countUserEvent(request.getApplicationId(), request.getUserId(),
				request.getStartDate(), request.getEndDate()).getCount();
	}

	private List<UserEvent> selectUserEvents(int page, int size) {
		logger.info("Selecting events startDate: {}, endDate: {} page: {}, size: {}", request.getStartDate(),
				request.getEndDate(), page, size);
		return getSparkService().getUserEventService().selectUserEvents(request.getApplicationId(), request.getUserId(),
				request.getStartDate(), request.getEndDate(), page, size);
	}

}
