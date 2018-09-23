package com.camp.applicationuserservice.client;

import org.springframework.stereotype.Component;

import com.camp.applicationuserservice.domain.Application;

@Component
public class ApplicationClientFallback implements ApplicationClient{

	@Override
	public Application getApplication(String id) {
		return new Application();
	}

}
