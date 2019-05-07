package com.binaryalchemist.programondo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.binaryalchemist.programondo.models.login.VerificationToken;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

	VerificationToken findVerificationTokenById(long tokenId); 
	VerificationToken findVerificationTokenByUserEmail(String userEmail); 
}
