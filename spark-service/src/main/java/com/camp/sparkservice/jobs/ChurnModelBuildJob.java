package com.camp.sparkservice.jobs;

import com.camp.sparkservice.domain.ChurnModelBuildRequest;
import com.camp.sparkservice.domain.SparkProcess;
import com.camp.sparkservice.service.SparkService;

public class ChurnModelBuildJob extends SparkProcess {

	private ChurnModelBuildRequest request;

	public ChurnModelBuildJob(ChurnModelBuildRequest request, SparkService sparkService) {
		super(sparkService);
		this.request = request;
	}


	@Override
	public void execute() {
		
		
	}

}
