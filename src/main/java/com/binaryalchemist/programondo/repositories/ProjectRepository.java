package com.binaryalchemist.programondo.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.binaryalchemist.programondo.models.Project;

@Repository
public interface ProjectRepository  extends CrudRepository<Project, Long> {

	Iterable<Project> findByUserName(String username);
	Iterable<Project> findByUserNameOrderByNameAsc(String username);
	Project findById(long id);
	
	@Query("select p from Project p where p.user.id = :userId AND p.selected = true")
	Project findActiveProject(long userId);

}
