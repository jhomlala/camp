package com.camp.applicationuserservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.camp.applicationuserservice.domain.Application;

@FeignClient( name = "application-service", fallback = ApplicationClientFallback.class)
public interface ApplicationClient {
	@RequestMapping(method = RequestMethod.GET, value = "/applications/{id}/",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Application getApplication(@PathVariable("id") String id);
}
