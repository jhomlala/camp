package com.camp.sparkservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.camp.sparkservice.domain.ChurnModelBuildRequest;
import com.camp.sparkservice.domain.ChurnPredictRequest;
import com.camp.sparkservice.service.SparkService;

@RestController
public class SparkController {

	@Autowired
	private SparkService sparkService;

	@RequestMapping(path = "/churn", method = RequestMethod.POST)
	public String buildChurnModel(@RequestBody ChurnModelBuildRequest churnModelBuildRequest) {
		return sparkService.process(churnModelBuildRequest);
	}
	
	@RequestMapping(path = "/churn/predict", method = RequestMethod.POST)
	public String predictChurn(@RequestBody ChurnPredictRequest churnModelBuildRequest) {
		return sparkService.process(churnModelBuildRequest);
	}

}
