package com.camp.applicationuserservice.repository;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;

import com.camp.applicationuserservice.domain.ApplicationUser;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUser, String> {
	@AllowFiltering
	ApplicationUser findByApplicationIdAndUsername(String id, String username);

	ApplicationUser findByIdAndApplicationId(String id, String applicationId);
}
