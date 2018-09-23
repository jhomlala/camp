package com.camp.applicationuserservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error_application_user_exists")
public class ApplicationUserExistsException extends RuntimeException{

	private static final long serialVersionUID = -158286288042886080L;

}
