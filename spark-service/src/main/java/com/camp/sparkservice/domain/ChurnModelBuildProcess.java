package com.camp.sparkservice.domain;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
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
		List<UserEvent> events = selectUserEvents(0,100);
		JavaRDD<UserEvent> eventsRDD = getSparkService().getSparkContext().parallelize(events);
		Dataset<Row> eventsDF = getSparkService().getSparkSession().createDataFrame(eventsRDD, UserEvent.class);
		Dataset<Row> categoryDF = eventsDF.select(new Column("category"));
		categoryDF.printSchema();
		
		this.setStatus(SparkProcessStatus.FINISHED);
		logger.info("Finished processing!");
	}

	private long countEvents() {
		return getSparkService().getUserEventService()
				.countUserEvent(request.getApplicationId(), request.getStartDate(), request.getEndDate()).getCount();
	}

	
	private List<UserEvent> selectUserEvents(int page, int size){
		return getSparkService().getUserEventService()
				.selectUserEvents(request.getApplicationId(), request.getStartDate(), request.getEndDate(),page,size);
	}
}
