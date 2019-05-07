package com.binaryalchemist.programondo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.binaryalchemist.programondo.models.login.PasswordToken;

@Repository
public interface PasswordTokenRepository extends CrudRepository<PasswordToken, Long> {
	PasswordToken findPasswordTokenById(long tokenId); 
	PasswordToken findPasswordTokenByUserEmail(String userEmail); 
}
