package com.camp.sparkservice.domain;

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
		Dataset<Row> uniqueUsersRegisteredDF = registerEventsDF
				.join(uniqueUsersDF, registerEventsDF.col("userId").equalTo(uniqueUsersDF.col("userId2")));
	
		uniqueUsersRegisteredDF = uniqueUsersRegisteredDF.select(uniqueUsersRegisteredDF.col("userId"),uniqueUsersRegisteredDF.col("createdAt"));
		uniqueUsersRegisteredDF.show();
		
		Dataset<Row> lastEventsDF = eventsDF.withColumn("createdAtTimestamp",eventsDF.col("createdAt").cast("timestamp"));
		lastEventsDF.show();
		lastEventsDF = lastEventsDF.groupBy("userId").agg(org.apache.spark.sql.functions.max("createdAtTimestamp").as("maxEventTime"));
		
		Dataset<Row> uniqueEventCategories = eventsDF.groupBy("category").count().select("category");
		List<Row> categoriesAsRowsList = uniqueEventCategories.collectAsList();
		List<String> categories = new ArrayList<String>();
		for (Row categoryRow: categoriesAsRowsList) {
			categories.add(categoryRow.mkString());
		}
		
		
		Dataset<Row> eventsCountDF = uniqueUsersDF;
		for (String category: categories) {
			logger.info("**** CATEGORY: {}",category);
			Dataset<Row> categoryDF = eventsDF.select("userId","category");
			categoryDF = categoryDF.where(categoryDF.col("category").equalTo(category)).groupBy("userId").count();
			categoryDF = categoryDF.withColumnRenamed("count", "count_"+category);
			categoryDF = categoryDF.withColumnRenamed("userId", "userId_"+category);
			eventsCountDF = eventsCountDF.join(categoryDF,eventsCountDF.col("userId2").equalTo(categoryDF.col("userId_"+category)));
		}
		eventsCountDF.show();
		
		


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
