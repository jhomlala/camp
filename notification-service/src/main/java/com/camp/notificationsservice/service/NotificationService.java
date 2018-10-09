package com.camp.notificationsservice.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.camp.notificationsservice.domain.Notification;
import com.camp.notificationsservice.domain.NotificationCreateRequest;
import com.camp.notificationsservice.domain.NotificationStatus;
import com.camp.notificationsservice.exception.InvalidNotificationIdException;
import com.camp.notificationsservice.exception.NotificationNotExistsException;
import com.camp.notificationsservice.repository.NotificationRepository;
import com.google.gson.Gson;

@Service
public class NotificationService {

	private final String topic = "notification_topic";
	
	@Autowired
	private KafkaTemplate<String, Notification> kafkaNotificationTemplate;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private Gson gson;
	
	public Notification createNotification(NotificationCreateRequest notificationCreateRequest) {
		Notification notification = new Notification();
		notification.setId(generateValidUUID());
		notification.setApplicationId(notificationCreateRequest.getApplicationId());
		notification.setTitle(notificationCreateRequest.getTitle());
		notification.setContent(notificationCreateRequest.getContent());
		notification.setStatus(NotificationStatus.PENDING.toString());
		notification.setAudience("ALL");
		notification.setCreatedAt(new Date());
		notification.setUpdatedAt(new Date());
		notification = notificationRepository.save(notification);
		return notification;
	}
	
	public Notification processCreateNotification(NotificationCreateRequest notificationCreateRequest) {
		Notification notification = createNotification(notificationCreateRequest);
		kafkaNotificationTemplate.send(topic, notification);
		return notification;
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
		return notificationRepository.findFirstByStatusOrderByCreatedAtDesc();
	}

	

}
