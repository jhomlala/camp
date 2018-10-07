package com.camp.usereventservice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.camp.usereventservice.domain.UserEvent;

public interface UserEventRepository extends CrudRepository<UserEvent, String> {

	@Query("SELECT * FROM UserEvent  WHERE applicationId = ?0 AND createdAt > ?1 AND createdAt < ?2")
	List<UserEvent> findByApplicationIdAndCreatedAtBetween(String applicationId, Date createdAtStart, Date createdAtEnd,
			Pageable pageable);

	@Query("SELECT COUNT(*) FROM UserEvent  WHERE applicationId = ?0 AND createdAt > ?1 AND createdAt < ?2")
	long countByApplicationIdAndCreatedAtBetween(String applicationId, Date createdAtStart, Date createdAtEnd);

	@Query("SELECT COUNT(*) FROM UserEvent  WHERE applicationId = ?0 AND createdAt > ?1 AND createdAt < ?2 AND userId = ?3 ALLOW FILTERING")
	long countByApplicationIdAndCreatedAtBetweenAndUserId(String applicationId, Date createdAtStart, Date createdAtEnd,
			String userId);

	@Query("SELECT * FROM UserEvent WHERE applicationId = ?0 AND createdAt > ?1 AND createdAt < ?2 AND userId = ?3 ALLOW FILTERING")
	List<UserEvent> findByApplicationIdAndCreatedAtBetweenAndUserId(String applicationId, Date createdAtStart,
			Date createdAtEnd, String userId, Pageable pageable);

}
