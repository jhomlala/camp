package com.camp.notificationsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "error_notification_not_exists")
public class NotificationNotExistsException extends RuntimeException {

	private static final long serialVersionUID = -8944572030122430789L;

}
