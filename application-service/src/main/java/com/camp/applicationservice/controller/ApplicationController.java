package com.camp.applicationservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.camp.applicationservice.domain.Application;
import com.camp.applicationservice.domain.ApplicationCreateRequest;
import com.camp.applicationservice.exception.ApplicationExistsException;
import com.camp.applicationservice.service.ApplicationService;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@RequestMapping(path = "/", method = RequestMethod.POST)
	public Application createApplication(@Valid @RequestBody ApplicationCreateRequest applicationCreateRequest) throws ApplicationExistsException {
		return applicationService.createApplication(applicationCreateRequest);
	}

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public Iterable<Application> selectApplications() {
		return applicationService.findAll();
	}
}
