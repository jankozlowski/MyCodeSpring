package com.binaryalchemist.programondo.repositories;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.binaryalchemist.programondo.models.CodeLog;
import com.binaryalchemist.programondo.models.Language;

@Repository
public interface LanguageRepository extends CrudRepository<Language, Long> {

	
	@Query("select l from Language l where l.codeLog.user.name = :user")
	Iterable<Language> findByUserName(String user);
	
	@Query("select l from Language l where l.codeLog.user.name = :user and l.codeLog.createdAt between :startDate and :endDate")
	Iterable<Language> findByUserNameAndDate(@Param("user")String user, @Param("startDate")Instant startDate, @Param("endDate")Instant endDate);
}

