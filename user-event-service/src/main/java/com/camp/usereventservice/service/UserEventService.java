package com.camp.usereventservice.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.camp.usereventservice.domain.CreateUserEventRequest;
import com.camp.usereventservice.domain.UserEvent;
import com.camp.usereventservice.repository.UserEventRepository;

@Service
public class UserEventService {

	private Logger logger = LoggerFactory.getLogger(UserEventService.class);

	@Autowired
	private UserEventRepository userEventRepository;

	public UserEvent createUserEvent(String applicationId, String userId,
			@Valid CreateUserEventRequest createUserEventRequest) {
		logger.info("Create user event for parameters: applicationId: {}, userId: {}. request: {}", applicationId,
				userId, createUserEventRequest);
		UserEvent userEvent = new UserEvent();
		userEvent.setApplicationId(applicationId);
		userEvent.setUserId(userId);
		userEvent.setCreatedAt(new Date());
		userEvent.setId(UUID.randomUUID().toString());
		userEvent.setCategory(createUserEventRequest.getCategory());
		userEvent.setContent(createUserEventRequest.getContent());
		return userEventRepository.save(userEvent);
	}

	public List<UserEvent> selectUserEvents(String applicationId, Date startDate, Date endDate, int page, int size) {
		logger.info("ApplicationId: {}, startDate: {}, endDate: {}, page: {}, size: {}", applicationId, startDate,
				endDate, page, size);
		PageRequest pageRequest = PageRequest.of(page, size);
		return userEventRepository.findByApplicationIdAndCreatedAtBetween(applicationId, startDate, endDate,
				pageRequest);
	}

	public long countUserEvents(String applicationId, Date startDate, Date endDate) {
		return userEventRepository.countByApplicationIdAndCreatedAtBetween(applicationId, startDate, endDate);
	}

}
