package com.camp.notificationsenderservice.service;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class NotificationSenderService {

	
	
	@Scheduled(fixedDelay = 5000)
	public void selectNextJob() {
		
	}
	
	
}
