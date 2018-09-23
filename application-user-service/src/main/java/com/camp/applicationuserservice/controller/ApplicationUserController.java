package com.camp.applicationuserservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.camp.applicationuserservice.domain.ApplicationUser;
import com.camp.applicationuserservice.domain.ApplicationUserCreateRequest;
import com.camp.applicationuserservice.domain.ApplicationUserUpdateRequest;
import com.camp.applicationuserservice.service.ApplicationUserService;

@RestController
public class ApplicationUserController {

	@Autowired
	private ApplicationUserService applicationUserService;

	@RequestMapping(path = "/{applicationId}", method = RequestMethod.POST)
	public ApplicationUser createApplicationUser(@PathVariable("applicationId") String applicationId,
			@Valid @RequestBody ApplicationUserCreateRequest applicationUserCreateRequest) {
		return applicationUserService.createApplicationUser(applicationId, applicationUserCreateRequest);
	}

	@RequestMapping(path = "/{applicationId}/{userId}", method = RequestMethod.PUT)
	public ApplicationUser updateApplicationUser(@PathVariable("applicationId") String applicationId,
			@PathVariable("userId") String userId,
			@Valid @RequestBody ApplicationUserUpdateRequest applicationUserUpdateRequest) {
		return applicationUserService.updateApplicationUser(userId,applicationId, applicationUserUpdateRequest);
	}

	@RequestMapping(path = "/{applicationId}/{userId}", method = RequestMethod.GET)
	public ApplicationUser selectApplicationUser(@PathVariable("applicationId") String applicationId,
			@PathVariable("userId") String userId) {
		return applicationUserService.findByIdAndApplicationId(userId, applicationId);
	}

}
