package com.camp.applicationuserservice.config;

import com.camp.applicationuserservice.exception.InvalidApplicationIdException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class ApplicationClientErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		return new InvalidApplicationIdException();
	}

}
