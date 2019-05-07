package com.binaryalchemist.programondo.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.binaryalchemist.programondo.models.Project;
import com.binaryalchemist.programondo.models.ProjectTask;


public interface ProjectTaskRepository extends CrudRepository<ProjectTask, Long> {

	@Query("select pt from ProjectTask pt inner join pt.project where pt.project.user.id = :userId order by pt.name asc")
	Iterable<ProjectTask> findByUserIdOrderByNameAsc(@Param("userId") long userId);
	ProjectTask findById(long id);
	@Query("select pt from ProjectTask pt inner join pt.project where pt.project.user.id = :userId AND pt.selected = true")
	ProjectTask findUserActiveTask(long userId);
}
