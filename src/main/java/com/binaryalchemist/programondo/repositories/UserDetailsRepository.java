package com.binaryalchemist.programondo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.models.UserDetails;

@Repository
public interface UserDetailsRepository extends CrudRepository<UserDetails, Long> {

}
