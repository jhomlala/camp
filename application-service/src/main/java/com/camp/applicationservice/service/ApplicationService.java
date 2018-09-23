package com.camp.applicationservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.camp.applicationservice.domain.Application;
import com.camp.applicationservice.domain.ApplicationCreateRequest;
import com.camp.applicationservice.domain.ApplicationUpdateRequest;
import com.camp.applicationservice.exception.ApplicationExistsException;
import com.camp.applicationservice.exception.ApplicationNotExistsException;
import com.camp.applicationservice.exception.InvalidApplicationIdException;
import com.camp.applicationservice.exception.InvalidGoogleServicesConfiguration;
import com.camp.applicationservice.repository.ApplicationRepository;

@Service
public class ApplicationService {

	private Logger logger = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	private ApplicationRepository applicationRepository;

	public Application createApplication(@Valid ApplicationCreateRequest applicationCreateRequest) {
		logger.info("Creating application from request: {}", applicationCreateRequest);
		if (findByPackageName(applicationCreateRequest.getPackageName()) != null) {
			throw new ApplicationExistsException();
		}

		Application application = new Application();
		application.setId(generateValidUUID());
		application.setName(applicationCreateRequest.getName());
		application.setOs(applicationCreateRequest.getOs());
		application.setPackageName(applicationCreateRequest.getPackageName());
		application.setCreatedAt(new Date());
		application.setUpdatedAt(new Date());
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

	public Application findById(String id) {
		logger.info("Find application by id: {}", id);
		if (!isIdValid(id)) {
			throw new InvalidApplicationIdException();
		}
		Optional<Application> applicationOptional = findByIdWithOptional(id);
		return applicationOptional.orElseThrow(ApplicationNotExistsException::new);
	}

	public Optional<Application> findByIdWithOptional(String id) {
		return applicationRepository.findById(id);
	}

	public Application updateApplication(String id, ApplicationUpdateRequest applicationUpdateRequest) {
		logger.info("Update application for id: {}, applicationUpdateRequest: {}", id, applicationUpdateRequest);
		Application application = findById(id);
		application.setName(applicationUpdateRequest.getName());
		application.setUpdatedAt(new Date());
		return applicationRepository.save(application);
	}

	private String generateValidUUID() {
		while (true) {
			String id = UUID.randomUUID().toString();
			if (!findByIdWithOptional(id).isPresent()) {
				return id;
			}
		}
	}

	private boolean isIdValid(String id) {
		return id != null && id.length() > 0;
	}

	public Application updateApplicationGoogleServices(String id, MultipartFile googleServicesFile) throws IOException {

		logger.info("Update application for id: {}, googleServicesFile: {}", id, googleServicesFile);
		Application application = findById(id);

		String googleServicesContent = new BufferedReader(new InputStreamReader(googleServicesFile.getInputStream()))
				.lines().collect(Collectors.joining("\n"));
		if (!valiateGoogleServicesContent(googleServicesContent)) {
			throw new InvalidGoogleServicesConfiguration();
		}
		application.setGoogleServicesConfiguration(googleServicesContent);
		applicationRepository.save(application);
		return application;
	}

	private boolean valiateGoogleServicesContent(String content) {
		return content != null && content.contains("project_info") && content.contains("api_key");
	}

}
