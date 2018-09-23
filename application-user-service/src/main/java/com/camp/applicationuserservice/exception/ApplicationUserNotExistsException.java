package com.camp.applicationuserservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "error_application_user_not_found")
public class ApplicationUserNotExistsException extends RuntimeException {

	private static final long serialVersionUID = 5308593178807806466L;

}
