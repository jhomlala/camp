package com.camp.sparkservice.domain;

import static org.apache.spark.sql.functions.when;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.camp.sparkservice.service.SparkService;
import com.google.common.reflect.TypeToken;

import org.apache.spark.sql.functions;
import scala.collection.immutable.Set;
import scala.collection.mutable.Buffer;

public class ChurnPredictionProcess extends SparkProcess {
	private final int PAGE_SIZE = 100;
	private static final long serialVersionUID = -2114214764831644182L;
	private Logger logger = LoggerFactory.getLogger(ChurnModelBuildProcess.class);

	private ChurnPredictRequest request;

	public ChurnPredictionProcess(SparkService sparkService, ChurnPredictRequest request) {
		super(sparkService);
		this.request = request;
	}

	@Override
	public void execute() {
		this.setStatus(SparkProcessStatus.PROCESSING);
		logger.info("Started churn mogel build process");

		/*
		 * Step 1: Model data setup
		 */
		long eventCount = countEvents();
		logger.info("Count of events, found for build process: {}", eventCount);
		List<UserEvent> events = loadUserEvents(eventCount);

		logger.info("Count of events downloaded from database for build process: {}", events.size());
		JavaRDD<UserEvent> eventsRDD = getSparkService().getSparkContext().parallelize(events);
		Dataset<Row> eventsDF = sparkSession().createDataFrame(eventsRDD, UserEvent.class);

		logger.info("Events:");
		eventsDF.show();
		
		List<String> categories = selectModelCategories();
		Dataset<Row> eventsCountDF = countUniqueEvents(eventsDF, categories);

		eventsCountDF.show();
		
		Dataset<Row> diffBetweenLastAndFirstEventDF = calculateDiffBetweenFirstAndLastEvent(eventsDF);
		Dataset<Row> usersDataDF = setupTrainingDF(eventsCountDF, diffBetweenLastAndFirstEventDF);
		logger.info(" Users DF: ");
		usersDataDF.show();
		String[] cols = usersDataDF.drop("label").columns();
		VectorAssembler vectorAssembler = new VectorAssembler().setInputCols(cols).setOutputCol("features");
		Dataset<Row> usersDataAssembledAsVector = vectorAssembler.transform(usersDataDF).select("features");
		usersDataAssembledAsVector.show();
		
		
		PipelineModel model = PipelineModel.load("/Users/user/Documents/models/churn/" + request.getApplicationId());
		Dataset<Row> predictions = model.transform(usersDataAssembledAsVector);
		
		logger.info("Predictions:");
		predictions.show();
		this.setStatus(SparkProcessStatus.FINISHED);
		logger.info("Finished processing!");
	}


	private List<String> selectModelCategories() {
		try {
			String categoriesJson = new String(Files.readAllBytes(
					Paths.get("/Users/user/Documents/models/churn/" + request.getApplicationId() + "/categories.json")));
			return getSparkService().getGson().fromJson(categoriesJson, new TypeToken<ArrayList<String>>(){}.getType());
			
		} catch (Exception exc) {
			exc.printStackTrace();
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
		logger.info("Categories: {}",categories);
		Dataset<Row> eventsCountDF = eventsDF.groupBy("userId").count().withColumnRenamed("userId", "userId2");
		Dataset<Row> mockDF = eventsCountDF.select("userId2");
		
		eventsCountDF.show();
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
			logger.info("CategoryDF:");
			categoryDF.show();
			
			eventsCountDF = eventsCountDF.join(categoryDF,
					eventsCountDF.col("userId2").equalTo(categoryDF.col("userId_" + category)));
			logger.info("After join: ");
			eventsCountDF.show();
			
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
		int selectIterations = (int) Math.ceil(count / (double) PAGE_SIZE);
		for (int iteration = 0; iteration <= selectIterations; iteration++) {
			userEvents.addAll(selectUserEvents(selectIterations, PAGE_SIZE));
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
