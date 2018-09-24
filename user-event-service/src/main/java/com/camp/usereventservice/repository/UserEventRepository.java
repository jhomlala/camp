package com.camp.usereventservice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.camp.usereventservice.domain.UserEvent;

public interface UserEventRepository extends CrudRepository<UserEvent, String> {
	
	@Query("SELECT * FROM UserEvent  WHERE applicationId = ?0 AND createdAt > ?1 AND createdAt < ?2")
	List<UserEvent> findByApplicationIdAndCreatedAtBetween(String applicationId,Date createdAtStart, Date createdAtEnd, Pageable pageable);
}
