package com.camp.applicationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error_application_exists")
public class ApplicationExistsException extends RuntimeException {

	private static final long serialVersionUID = 5924618531285769373L;

}
