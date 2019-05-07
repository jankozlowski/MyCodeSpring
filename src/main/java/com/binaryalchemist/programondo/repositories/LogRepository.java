package com.binaryalchemist.programondo.repositories;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.binaryalchemist.programondo.models.CodeLog;

@Repository
public interface LogRepository  extends CrudRepository<CodeLog, Long> {
	Page<CodeLog> findByUserName(String user, Pageable pageable);
	int countByUserName(String user);
	Iterable<CodeLog> findByUserName(String user);
	@Query("select c from CodeLog c where c.user.name = :user and c.createdAt between :startDate and :endDate")
	Iterable<CodeLog> findByUserNameAndDate(@Param("user")String user, @Param("startDate")Instant startDate, @Param("endDate")Instant endDate);

}
