package com.camp.sparkservice.client;

import java.util.Date;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.camp.sparkservice.domain.UserEvent;
import com.camp.sparkservice.domain.UserEventCount;

@FeignClient(name = "user-event-service", fallback = UserEventClientFallback.class)
public interface UserEventClient {
	@RequestMapping(method = RequestMethod.GET, value = "/userevent/{applicationId}/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	List<UserEvent> getUserEvents(@PathVariable("applicationId") String applicationId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam("page") int page, @RequestParam("size") int size);

	@RequestMapping(method = RequestMethod.GET, value = "/userevent/{applicationId}/count/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	UserEventCount countUserEvents(@PathVariable("applicationId") String applicationId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);

	@RequestMapping(method = RequestMethod.GET, value = "/userevent/{applicationId}/{userId}/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	List<UserEvent> getUserEvents(@PathVariable("applicationId") String applicationId,
			@PathVariable("userId") String userId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam("page") int page, @RequestParam("size") int size);

	@RequestMapping(method = RequestMethod.GET, value = "/userevent/{applicationId}/{userId}/count/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	UserEventCount countUserEvents(@PathVariable("applicationId") String applicationId,
			@PathVariable("userId") String userId,
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);

}
