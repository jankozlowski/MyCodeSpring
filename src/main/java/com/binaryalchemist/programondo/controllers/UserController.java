package com.binaryalchemist.programondo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.services.UserService;
import com.binaryalchemist.programondo.services.UserServiceImpl;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	
	@PostMapping("")
	public ResponseEntity<?> addUserToDb(@RequestBody User user, BindingResult results){
		
		if(results.hasErrors()) {
			System.out.println("some errors");
			System.out.println(results.getGlobalError());
			return new ResponseEntity<Map<String,String>>(HttpStatus.BAD_REQUEST);
			
		}
		
		System.out.println("no errors");
		User responseUser = userServiceImpl.saveOrUpdateUser(user);
		return new ResponseEntity<User>(responseUser, HttpStatus.CREATED);
		
	}
	
	@GetMapping("")
	public void gettest() {
		System.out.println("Get my test");
	}
	@GetMapping("/all")
	public Iterable<User> getAllUsers() {
		return userServiceImpl.findAll();
	}
	@GetMapping("/email/{userEmail}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String userEmail) {
		User user = userServiceImpl.findByEmail(userEmail);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	@GetMapping("/name/{userName}")
	public ResponseEntity<?> getUserByName(@PathVariable String userName) {
		User user = userServiceImpl.findByUsername(userName);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	

}
