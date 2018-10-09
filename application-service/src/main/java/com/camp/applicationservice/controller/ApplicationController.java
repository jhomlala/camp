package com.camp.applicationservice.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.camp.applicationservice.domain.Application;
import com.camp.applicationservice.domain.ApplicationCreateRequest;
import com.camp.applicationservice.domain.ApplicationUpdateRequest;
import com.camp.applicationservice.service.ApplicationService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@HystrixCommand(fallbackMethod = "createApplicationFallback")
	@RequestMapping(path = "/", method = RequestMethod.POST)
	public Application createApplication(@Valid @RequestBody ApplicationCreateRequest applicationCreateRequest) {
		return applicationService.createApplication(applicationCreateRequest);
	}

	@HystrixCommand(fallbackMethod = "selectApplicationsFallback")
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public Iterable<Application> selectApplications() {
		return applicationService.findAll();
	}

	@HystrixCommand(fallbackMethod = "selectApplicationFallback")
	@RequestMapping(path = "/{id}/", method = RequestMethod.GET)
	public Application selectApplication(@PathVariable String id) {
		return applicationService.findById(id);
	}

	@HystrixCommand(fallbackMethod = "updateApplicationFallback")
	@RequestMapping(path = "/{id}/", method = RequestMethod.PUT)
	public Application updateApplication(@PathVariable String id,
			@RequestBody ApplicationUpdateRequest applicationUpdateRequest) {
		return applicationService.updateApplication(id, applicationUpdateRequest);
	}

	@HystrixCommand(fallbackMethod = "updateApplicationGoogleServicesFallback")
	@RequestMapping(path = "/{id}/googleservices/", method = RequestMethod.PUT)
	public Application updateGoogleServices(@PathVariable String id, @RequestParam("file") MultipartFile multiPartFile)
			throws IOException {
		return applicationService.updateApplicationGoogleServices(id, multiPartFile);
	}

	public Iterable<Application> selectApplicationsFallback() {
		return new ArrayList<>();
	}

	public Application createApplicationFallback(ApplicationCreateRequest applicationCreateRequest) {
		return new Application();
	}

	public Application selectApplicationFallback(String id) {
		return new Application();
	}

	public Application updateApplicationFallback(String id, ApplicationUpdateRequest applicationUpdateRequest) {
		return new Application();
	}

	public Application updateApplicationGoogleServicesFallback(String id, MultipartFile multiPartFile) {
		return new Application();
	}

}
