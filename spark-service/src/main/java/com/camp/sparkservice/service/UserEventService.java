package com.camp.sparkservice.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camp.sparkservice.client.UserEventClient;
import com.camp.sparkservice.domain.UserEvent;
import com.camp.sparkservice.domain.UserEventCount;

@Service
public class UserEventService {
	private Logger logger = LoggerFactory.getLogger(UserEventService.class);
	@Autowired
	private UserEventClient userEventClient;

	public List<UserEvent> selectUserEvents(String applicationId, Date startDate, Date endDate, int page, int size) {
		return userEventClient.getUserEvents(applicationId, startDate, endDate, page, size);
	}

	public UserEventCount countUserEvent(String applicationId, Date startDate, Date endDate) {
		logger.info("ApplicationId: {}, startDate: {}, endDate: {}", applicationId, startDate, endDate);
		return userEventClient.countUserEvents(applicationId, startDate, endDate);
	}
}
