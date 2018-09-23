package com.camp.applicationuserservice.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camp.applicationuserservice.client.ApplicationClient;
import com.camp.applicationuserservice.domain.Application;
import com.camp.applicationuserservice.domain.ApplicationUser;
import com.camp.applicationuserservice.domain.ApplicationUserCreateRequest;
import com.camp.applicationuserservice.domain.ApplicationUserUpdateRequest;
import com.camp.applicationuserservice.exception.ApplicationUserExistsException;
import com.camp.applicationuserservice.exception.ApplicationUserNotExistsException;
import com.camp.applicationuserservice.exception.InvalidApplicationIdException;
import com.camp.applicationuserservice.exception.InvalidApplicationUserIdException;
import com.camp.applicationuserservice.repository.ApplicationUserRepository;

@Service
public class ApplicationUserService {

	private Logger logger = LoggerFactory.getLogger(ApplicationUserService.class);

	@Autowired
	private ApplicationUserRepository applicationUserRepository;

	@Autowired
	private ApplicationClient applicationClient;

	public ApplicationUser createApplicationUser(ApplicationUserCreateRequest applicationUserCreateRequest) {
		logger.info("Creating application from request: {}", applicationUserCreateRequest);
		if (findByApplicationIdAndUsername(applicationUserCreateRequest.getApplicationId(),
				applicationUserCreateRequest.getUsername()) != null) {
			throw new ApplicationUserExistsException();
		}
		Application application = applicationClient.getApplication(applicationUserCreateRequest.getApplicationId());
		if (application == null || application.getId() == null) {
			throw new InvalidApplicationIdException();
		}
		logger.info("Selected application: {}", application);

		ApplicationUser applicationUser = new ApplicationUser();
		applicationUser.setId(generateValidUUID(applicationUserCreateRequest.getApplicationId()));
		applicationUser.setApplicationId(applicationUserCreateRequest.getApplicationId());
		applicationUser.setUsername(applicationUserCreateRequest.getUsername());
		applicationUser.setCreatedAt(new Date());
		applicationUser.setUpdatedAt(new Date());
		applicationUser.setFirebaseToken(applicationUserCreateRequest.getFirebaseToken());

		applicationUserRepository.save(applicationUser);

		return applicationUser;

	}

	public ApplicationUser findByApplicationIdAndUsername(String applicationId, String username) {
		return applicationUserRepository.findByApplicationIdAndUsername(applicationId, username);
	}

	public ApplicationUser findByIdAndApplicationId(String userId, String applicationId) {
		return applicationUserRepository.findByIdAndApplicationId(userId, applicationId);
	}

	private String generateValidUUID(String applicationId) {
		while (true) {
			String id = UUID.randomUUID().toString();
			if (findByApplicationIdAndUsername(applicationId, id) == null) {
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

	public ApplicationUser updateApplicationUser(String userId,
			@Valid ApplicationUserUpdateRequest applicationUserUpdateRequest) {
		logger.info("Updating application user for applicationUserUpdateRequest: {}", applicationUserUpdateRequest);

		if (!isIdValid(userId)) {
			throw new InvalidApplicationUserIdException();
		}
		ApplicationUser applicationUser = findByIdAndApplicationId(userId,
				applicationUserUpdateRequest.getApplicationId());
		logger.info("Application user: userId: {}, applicationId: {}", userId,
				applicationUserUpdateRequest.getApplicationId());
		if (applicationUser == null) {
			logger.info("Application user not found");
			throw new ApplicationUserNotExistsException();
		}
		applicationUser.setFirebaseToken(applicationUserUpdateRequest.getFirebaseToken());
		applicationUserRepository.save(applicationUser);

		return applicationUser;
	}

}
