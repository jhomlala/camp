package com.camp.notificationsservice.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.camp.notificationsservice.domain.Notification;
import com.camp.notificationsservice.domain.NotificationCreateRequest;

@Service
public class NotificationService {

	public Notification createNotification(@Valid NotificationCreateRequest notificationCreateRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
