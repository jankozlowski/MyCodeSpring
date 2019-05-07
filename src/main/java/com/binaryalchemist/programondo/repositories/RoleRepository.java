package com.binaryalchemist.programondo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.binaryalchemist.programondo.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findById(long id);

	Role findByName(String string);
}

