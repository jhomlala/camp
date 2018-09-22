package com.camp.applicationservice.repository;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;

import com.camp.applicationservice.domain.Application;

public interface ApplicationRepository extends CrudRepository<Application,String>{
	@AllowFiltering
	Application findByName(String name);
	@AllowFiltering
	Application findByPackageName(String packageName);
}
