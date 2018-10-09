package com.camp.usereventservice.controller;

import java.util.Collections;
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
import com.camp.usereventservice.domain.UserEventCount;
import com.camp.usereventservice.service.UserEventService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class UserEventController {

	@Autowired
	private UserEventService userEventService;

	@HystrixCommand(fallbackMethod = "createUserEventFallback")
	@RequestMapping(path = "/{applicationId}/{userId}/", method = RequestMethod.POST)
	public UserEvent createUserEvent(@PathVariable("applicationId") String applicationId,
			@PathVariable("userId") String userId, @Valid @RequestBody CreateUserEventRequest createUserEventRequest) {
		return userEventService.createUserEvent(applicationId, userId, createUserEventRequest);
	}

	@HystrixCommand(fallbackMethod = "selectUserEventsFallback")
	@RequestMapping(path = "/{applicationId}/", method = RequestMethod.GET)
	public List<UserEvent> selectUserEvents(@PathVariable("applicationId") String applicationId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		return userEventService.selectUserEvents(applicationId, startDate, endDate, page, size);
	}

	@HystrixCommand(fallbackMethod = "countUserEventsCountFallback")
	@RequestMapping(path = "/{applicationId}/count/", method = RequestMethod.GET)
	public UserEventCount countUserEventsCount(@PathVariable("applicationId") String applicationId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		return new UserEventCount(userEventService.countUserEvents(applicationId, startDate, endDate));
	}

	@HystrixCommand(fallbackMethod = "selectUserEventsFallback")
	@RequestMapping(path = "/{applicationId}/{userId}", method = RequestMethod.GET)
	public List<UserEvent> selectUserEvents(@PathVariable("applicationId") String applicationId,
			@PathVariable("userId") String userId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		return userEventService.selectUserEvents(applicationId, userId, startDate, endDate, page, size);
	}

	@HystrixCommand(fallbackMethod = "countUserEventsCountFallback")
	@RequestMapping(path = "/{applicationId}/{userId}/count/", method = RequestMethod.GET)
	public UserEventCount countUserEventsCount(@PathVariable("applicationId") String applicationId,
			@PathVariable("userId") String userId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		return new UserEventCount(userEventService.countUserEvents(applicationId, userId, startDate, endDate));
	}

	public UserEvent createUserEventFallback(String applicationId, String userId,
			CreateUserEventRequest createUserEventRequest) {
		return new UserEvent();
	}

	public List<UserEvent> selectUserEventsFallback(String applicationId, Date startDate, Date endDate, int size) {
		return Collections.emptyList();
	}

	public UserEventCount countUserEventsCountFallback(String applicationId, Date startDate, Date endDate) {
		return new UserEventCount(0);
	}

	public List<UserEvent> selectUserEventsFallback(String applicationId, String userId, Date startDate, Date endDate,
			int size) {
		return Collections.emptyList();
	}

	public UserEventCount countUserEventsCountFallback(String applicationId, String userId, Date startDate,
			Date endDate) {
		return new UserEventCount(0);
	}
}
