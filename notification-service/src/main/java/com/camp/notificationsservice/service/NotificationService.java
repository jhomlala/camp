package com.camp.notificationsservice.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camp.notificationsservice.domain.Notification;
import com.camp.notificationsservice.domain.NotificationCreateRequest;
import com.camp.notificationsservice.domain.NotificationStatus;
import com.camp.notificationsservice.exception.InvalidNotificationIdException;
import com.camp.notificationsservice.exception.NotificationNotExistsException;
import com.camp.notificationsservice.repository.NotificationRepository;

@Service
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;
	
	public Notification createNotification(@Valid NotificationCreateRequest notificationCreateRequest) {
		Notification notification = new Notification();
		notification.setId(generateValidUUID());
		notification.setApplicationId(notificationCreateRequest.getApplicationId());
		notification.setTitle(notificationCreateRequest.getTitle());
		notification.setContent(notificationCreateRequest.getContent());
		notification.setStatus(NotificationStatus.PENDING.toString());
		notification.setAudience("ALL");
		notification.setCreatedAt(new Date());
		notification.setUpdatedAt(new Date());
		return notificationRepository.save(notification);
	}
	
	public Optional<Notification> findByIdWithOptional(String id){
		return notificationRepository.findById(id);
	}
	
	public Notification findById(String id){
		if(!isIdValid(id)) {
			throw new InvalidNotificationIdException();
		}
		return notificationRepository.findById(id).orElseThrow(NotificationNotExistsException::new);
	}
	
	private String generateValidUUID() {
		while (true) {
			String id = UUID.randomUUID().toString();
			if (!findByIdWithOptional(id).isPresent()) {
				return id;
			}
		}
	}

	private boolean isIdValid(String id) {
		return id != null && id.length() > 0;
	}

	

	public Notification findFirstPendingNotification() {
		return notificationRepository.findFirstByStatusOrderByCreatedAdDesc();
	}

}
