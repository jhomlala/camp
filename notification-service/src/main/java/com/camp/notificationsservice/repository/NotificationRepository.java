package com.camp.notificationsservice.repository;

import org.springframework.data.repository.CrudRepository;

import com.camp.notificationsservice.domain.Notification;

public interface NotificationRepository extends CrudRepository<Notification, String> {

}
