package com.camp.applicationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "error_application_not_exists")
public class ApplicationNotExistsException extends RuntimeException {

	private static final long serialVersionUID = 5211375356720677414L;

}
