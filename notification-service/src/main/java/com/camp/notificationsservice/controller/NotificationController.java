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
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;



@Controller
public class NotificationController {
	@Autowired
	private NotificationService notificationService;

	@HystrixCommand(fallbackMethod = "createApplicationUserFallback")
	@RequestMapping(path = "/", method = RequestMethod.POST)
	public Notification createNotification(@Valid @RequestBody NotificationCreateRequest notificationCreateRequest) {
		return notificationService.createNotification(notificationCreateRequest);
	}
	
	@HystrixCommand(fallbackMethod = "selectFirstPendingNotificationFallback")
	@RequestMapping(path = "/pending/", method = RequestMethod.POST)
	public Notification selectFirstPendingNotification() {
		return notificationService.findFirstPendingNotification();
	}
	
	public Notification createNotificationFallback(NotificationCreateRequest notificationCreateRequest) {
		return new Notification();
	}
	
	public Notification selectFirstPendingNotificationFallback() {
		return new Notification();
	}
}
