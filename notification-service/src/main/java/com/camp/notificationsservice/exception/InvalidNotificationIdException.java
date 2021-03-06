package com.camp.notificationsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error_invalid_application_id")
public class InvalidNotificationIdException extends RuntimeException {

	private static final long serialVersionUID = 994239015391966402L;

}
