package com.binaryalchemist.programondo.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.binaryalchemist.programondo.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	User findByName(String name);
	User findByEmail(String email);
	User findById(long id);
    Boolean existsByName(String name);
    Boolean existsByEmail(String email);
    
    @Query("from User u join u.userDetails where u.name = :name")
	User findByNameLoadOnlyUserAndUserDetails(String name);
}
