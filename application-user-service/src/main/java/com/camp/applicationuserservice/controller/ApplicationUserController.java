package com.camp.applicationuserservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.camp.applicationuserservice.domain.ApplicationUser;
import com.camp.applicationuserservice.domain.ApplicationUserCreateRequest;
import com.camp.applicationuserservice.service.ApplicationUserService;

@RestController
public class ApplicationUserController {

	@Autowired
	private ApplicationUserService applicationUserService;

	@RequestMapping(path = "/", method = RequestMethod.POST)
	public ApplicationUser createApplicationUser(
			@Valid @RequestBody ApplicationUserCreateRequest applicationUserCreateRequest) {
		return applicationUserService.createApplicationUser(applicationUserCreateRequest);
	}
}
