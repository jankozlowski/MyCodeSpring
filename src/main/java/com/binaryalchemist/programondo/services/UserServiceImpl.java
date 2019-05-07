package com.binaryalchemist.programondo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	public User saveOrUpdateUser(User user) {
		return userRepository.save(user);
	}
	
	public Iterable<User> findAll(){
		return userRepository.findAll();
	}


	public User findByUsername(String name) {
		return userRepository.findByName(name);
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}


	public User findById(int id) {
		return userRepository.findById(id);
	}
}

