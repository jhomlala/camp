package com.camp.applicationservice.service;

import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camp.applicationservice.domain.Application;
import com.camp.applicationservice.domain.ApplicationCreateRequest;
import com.camp.applicationservice.exception.ApplicationExistsException;
import com.camp.applicationservice.repository.ApplicationRepository;

@Service
public class ApplicationService {

	private Logger logger = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	private ApplicationRepository applicationRepository;

	public Application createApplication(@Valid ApplicationCreateRequest applicationCreateRequest) throws ApplicationExistsException {
		if (findByPackageName(applicationCreateRequest.getPackageName()) != null) {
			throw new ApplicationExistsException("packageName",applicationCreateRequest.getPackageName());
		}
		
		
		Application application = new Application();
		application.setId(UUID.randomUUID());
		application.setName(applicationCreateRequest.getName());
		application.setOs(applicationCreateRequest.getOs());
		application.setPackageName(applicationCreateRequest.getPackageName());
		application = applicationRepository.save(application);
		logger.info("Created application: {}", application);
		return application;
	}

	public Iterable<Application> findAll() {
		return applicationRepository.findAll();
	}
	
	public Application findByPackageName(String packageName) {
		return applicationRepository.findByPackageName(packageName);
	}
}
