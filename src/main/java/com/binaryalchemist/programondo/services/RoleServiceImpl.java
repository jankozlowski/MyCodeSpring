package com.binaryalchemist.programondo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binaryalchemist.programondo.repositories.RoleRepository;
import com.binaryalchemist.programondo.repositories.UserRepository;

@Service
public class RoleServiceImpl {

	@Autowired
	private RoleRepository roleRepository;

}
