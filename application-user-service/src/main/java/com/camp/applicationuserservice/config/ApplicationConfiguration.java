package com.camp.applicationuserservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
	@Bean
	public ApplicationClientErrorDecoder applicationClientErrorDecoder() {
	  return new ApplicationClientErrorDecoder();
	}
}
