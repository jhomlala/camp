package com.camp.applicationuserservice.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camp.applicationuserservice.domain.ApplicationUser;
import com.camp.applicationuserservice.domain.ApplicationUserCreateRequest;
import com.camp.applicationuserservice.exception.ApplicationUserExistsException;
import com.camp.applicationuserservice.exception.ApplicationUserNotExistsException;
import com.camp.applicationuserservice.exception.InvalidApplicationUserIdException;
import com.camp.applicationuserservice.repository.ApplicationUserRepository;

@Service
public class ApplicationUserService {

	private Logger logger = LoggerFactory.getLogger(ApplicationUserService.class);

	@Autowired
	private ApplicationUserRepository applicationUserRepository;

	public ApplicationUser createApplicationUser(ApplicationUserCreateRequest applicationUserCreateRequest) {
		logger.info("Creating application from request: {}", applicationUserCreateRequest);
		if (findByApplicationIdAndUsername(applicationUserCreateRequest.getApplicationId(),
				applicationUserCreateRequest.getUsername()) != null) {
			throw new ApplicationUserExistsException();
		}

		ApplicationUser applicationUser = new ApplicationUser();
		applicationUser.setApplicationId(applicationUserCreateRequest.getApplicationId());
		applicationUser.setUsername(applicationUserCreateRequest.getUsername());
		applicationUser.setCreatedAt(new Date());
		applicationUser.setUpdatedAt(new Date());
		applicationUser.setFirebaseToken(applicationUserCreateRequest.getFirebaseToken());

		return applicationUser;

	}

	public ApplicationUser findById(String id) {
		logger.info("Find application by id: {}", id);
		if (!isIdValid(id)) {
			throw new InvalidApplicationUserIdException();
		}
		Optional<ApplicationUser> applicationOptional = findByIdWithOptional(id);
		return applicationOptional.orElseThrow(ApplicationUserNotExistsException::new);
	}

	public ApplicationUser findByApplicationIdAndUsername(String id, String username) {
		return applicationUserRepository.findByApplicationIdAndUsername(id, username);
	}

	private String generateValidUUID() {
		while (true) {
			String id = UUID.randomUUID().toString();
			if (!findByIdWithOptional(id).isPresent()) {
				return id;
			}
		}
	}

	public Optional<ApplicationUser> findByIdWithOptional(String id) {
		return applicationUserRepository.findById(id);
	}

	private boolean isIdValid(String id) {
		return id != null && id.length() > 0;
	}

}
