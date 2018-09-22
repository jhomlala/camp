package com.camp.applicationservice.repository;
import org.springframework.data.repository.CrudRepository;

import com.camp.applicationservice.domain.Application;

public interface ApplicationRepository extends CrudRepository<Application,String>{
	
	Application findByName(String name);
}
