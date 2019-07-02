package com.binaryalchemist.programondo.controllers;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binaryalchemist.programondo.models.ApiResponse;
import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.models.UserDetails;
import com.binaryalchemist.programondo.models.UserDetailsRequest;
import com.binaryalchemist.programondo.models.login.PasswordChangeRequest;
import com.binaryalchemist.programondo.repositories.UserDetailsRepository;
import com.binaryalchemist.programondo.repositories.UserRepository;
import com.binaryalchemist.programondo.services.UserService;
import com.binaryalchemist.programondo.services.UserServiceImpl;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	private UserDetailsRepository userDetailsRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
	
	
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
	@GetMapping("/email/{useremail}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String useremail) {
		User user = userServiceImpl.findByEmail(useremail);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	@GetMapping("/name/{username}")
	public ResponseEntity<?> getUserByName(@PathVariable String username) {
		User user = userServiceImpl.findByUsername(username);
		
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@PostMapping("/details/{username}")
	public ResponseEntity<?> updateUserDetails(@PathVariable String username, @RequestBody UserDetailsRequest userDetailsRequest) {
		User user = userServiceImpl.findByUsername(username);
		user.setUpdatedAt(Instant.now());
		
		UserDetails userDetails = new UserDetails();
		userDetails.setUser(user);
		userDetails.setBirthday(userDetailsRequest.getBirthday());
		userDetails.setCity(userDetailsRequest.getCity());
		userDetails.setCountry(userDetailsRequest.getCountry());
		userDetails.setGender(userDetailsRequest.isGender());
		userDetails.setOccupation(userDetailsRequest.getOccupation());
		userDetails.setId(user.getId());
		
		userDetailsRepository.save(userDetails);
		userRepository.save(user);
		
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
    @PostMapping("/changePassword/{username}")
    public ResponseEntity<?> changePasswordInPanel(@PathVariable String username, @RequestBody PasswordChangeRequest newPassword) {
		
    	User user = userServiceImpl.findByUsername(username);	
    	
    	if (!new BCryptPasswordEncoder().matches(newPassword.getOldPassword(), user.getPassword())) {
        	return new ResponseEntity(new ApiResponse(false, "Wrong Old password"),
                    HttpStatus.BAD_REQUEST);
        }
      
    	user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
    	
    	user.setUpdatedAt(Instant.now());
    	
    	userRepository.save(user);
    		
    	 return new ResponseEntity(new ApiResponse(true, "Password changed"),
                 HttpStatus.OK);
    	
    }
	

}
