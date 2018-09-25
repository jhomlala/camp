package com.camp.sparkservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.camp.sparkservice.jobs.ExampleJob;
import com.camp.sparkservice.service.SparkService;


@RestController
public class ApplicationUserController {

	
	@Autowired
	private SparkService sparkService;

	@Autowired
	private ExampleJob job;
	
	@RequestMapping(path = "/test", method = RequestMethod.GET)
	public String createApplicationUser() {
		sparkService.startJob(job);
		return "OK";
	}


}
