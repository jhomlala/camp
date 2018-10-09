package com.camp.notificationsservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

@Configuration
public class ApplicationConfig {

	@Bean
	public Gson gson() {
		return new Gson();
	}
	
}
