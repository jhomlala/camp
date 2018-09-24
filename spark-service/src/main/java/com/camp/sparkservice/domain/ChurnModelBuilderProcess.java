package com.camp.sparkservice.domain;

import org.apache.spark.SparkContext;

public class ChurnModelBuilderProcess extends SparkProcess {

	public ChurnModelBuilderProcess(SparkContext sparkContext,ChurnModelBuildRequest request) {
		super(sparkContext);
	}

	/*
	 * 1. Load data from UserEvent table for given application
	 * 2. If data fits in memory, process it, otherwise save it to temp file in disk
	 * 3. Load data into spark
	 * 4. Find top events without sing-in event
	 * 5. Find unique users 
	 * 6. Calculate counts for events for each user
	 * 7. Decide which user is out of app (inactive more than x days)
	 * 8. Generate GBT or other tree based algorithm
	 * 9. Test model (AUROC, matrix)
	 * 10 Save model if ok otherwise inform about lack of data
	 */
	@Override
	public void execute() {
		// TODO Auto-generated method stu
		
	}

}
