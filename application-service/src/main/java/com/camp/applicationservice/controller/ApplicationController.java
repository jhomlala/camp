package com.camp.applicationservice.controller;

import java.io.IOException;

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

@RestController
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@RequestMapping(path = "/", method = RequestMethod.POST)
	public Application createApplication(@Valid @RequestBody ApplicationCreateRequest applicationCreateRequest) {
		return applicationService.createApplication(applicationCreateRequest);
	}

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public Iterable<Application> selectApplications() {
		return applicationService.findAll();
	}

	@RequestMapping(path = "/{id}/", method = RequestMethod.GET)
	public Application selectApplication(@PathVariable String id) {
		return applicationService.findById(id);
	}

	@RequestMapping(path = "/{id}/", method = RequestMethod.PUT)
	public Application updateApplication(@PathVariable String id, @RequestBody ApplicationUpdateRequest applicationUpdateRequest) {
		return applicationService.updateApplication(id, applicationUpdateRequest);
	}
	
	@RequestMapping(path = "/{id}/googleservices/", method = RequestMethod.PUT)
	public Application updateGoogleServices(@PathVariable String id, @RequestParam("file") MultipartFile multiPartFile) throws IOException {
		return applicationService.updateApplicationGoogleServices(id, multiPartFile);
	}
	
}
