package com.camp.notificationsservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.camp.notificationsservice.domain.Notification;
import com.camp.notificationsservice.domain.NotificationCreateRequest;
import com.camp.notificationsservice.service.NotificationService;



@Controller
public class NotificationController {
	@Autowired
	private NotificationService notificationService;

	@RequestMapping(path = "/", method = RequestMethod.POST)
	public Notification createNotification(@Valid @RequestBody NotificationCreateRequest notificationCreateRequest) {
		return notificationService.createNotification(notificationCreateRequest);
	}
	
	@RequestMapping(path = "/pending/", method = RequestMethod.POST)
	public Notification selectFirstPendingNotification() {
		return notificationService.findFirstPendingNotification();
	}
}
