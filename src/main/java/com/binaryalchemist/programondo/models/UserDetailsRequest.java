package com.binaryalchemist.programondo.models;

import com.binaryalchemist.programondo.models.login.LoginRequest;

import lombok.Data;

@Data
public class UserDetailsRequest {
	  
	private String country;
	private String city;
	private String occupation;
	private String birthday;
	private boolean gender;
}
