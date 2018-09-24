package com.camp.usereventservice.controller;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.camp.usereventservice.domain.CreateUserEventRequest;
import com.camp.usereventservice.domain.UserEvent;
import com.camp.usereventservice.service.UserEventService;

@RestController
public class UserEventController {

	@Autowired
	private UserEventService userEventService;

	@RequestMapping(path = "/{applicationId}/{userId}/", method = RequestMethod.POST)
	public UserEvent createUserEvent(@PathVariable("applicationId") String applicationId,
			@PathVariable("userId") String userId, @Valid @RequestBody CreateUserEventRequest createUserEventRequest) {
		return userEventService.createUserEvent(applicationId, userId, createUserEventRequest);
	}

	@RequestMapping(path = "/{applicationId}/", method = RequestMethod.GET)
	public List<UserEvent> selectUserEvents(@PathVariable("applicationId") String applicationId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		return userEventService.selectUserEvents(applicationId, startDate, endDate, page, size);
	}

}
