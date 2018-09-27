package com.camp.sparkservice.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.camp.sparkservice.domain.UserEvent;
import com.camp.sparkservice.domain.UserEventCount;


@Component
public class UserEventClientFallback implements UserEventClient{

	@Override
	public List<UserEvent> getUserEvents(String applicationId, Date startDate, Date endDate, int page, int size) {
		return Collections.emptyList();
	}

	@Override
	public UserEventCount countUserEvents(String applicationId, Date startDate, Date endDate) {
		return new UserEventCount(0l);
	}



}
