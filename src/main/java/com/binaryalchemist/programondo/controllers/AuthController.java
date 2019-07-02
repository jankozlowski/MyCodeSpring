package com.binaryalchemist.programondo.controllers;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.binaryalchemist.programondo.mail.EmailServiceImpl;
import com.binaryalchemist.programondo.models.ApiResponse;
import com.binaryalchemist.programondo.models.Role;
import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.models.UserDetails;
import com.binaryalchemist.programondo.constants.Constants;
import com.binaryalchemist.programondo.models.login.JwtAuthenticationResponse;
import com.binaryalchemist.programondo.models.login.LoginRequest;
import com.binaryalchemist.programondo.models.login.PasswordChangeRequest;
import com.binaryalchemist.programondo.models.login.PasswordToken;
import com.binaryalchemist.programondo.models.login.SignUpRequest;
import com.binaryalchemist.programondo.models.login.TokenRequest;
import com.binaryalchemist.programondo.models.login.VerificationToken;
import com.binaryalchemist.programondo.repositories.PasswordTokenRepository;
import com.binaryalchemist.programondo.repositories.RoleRepository;
import com.binaryalchemist.programondo.repositories.UserDetailsRepository;
import com.binaryalchemist.programondo.repositories.UserRepository;
import com.binaryalchemist.programondo.repositories.VerificationTokenRepository;
import com.binaryalchemist.programondo.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsRepository userDetailsRepository;
    
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    PasswordTokenRepository passwordTokenRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;
    
    @Autowired
    EmailServiceImpl mail;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, Errors errors) {
    	
    	
    	User user = userRepository.findByName(loginRequest.getName());
    	
        if (user == null) {
        	return new ResponseEntity(new ApiResponse(false, "User not found"),
                    HttpStatus.BAD_REQUEST);
        }
       
        if (!new BCryptPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword())) {
        	return new ResponseEntity(new ApiResponse(false, "Wrong password"),
                    HttpStatus.BAD_REQUEST);
        }
        
        if(!user.isActive()) {
        	return new ResponseEntity(new ApiResponse(false, "User not active"),
                    HttpStatus.BAD_REQUEST);
        }
    	
    	
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getName(),
                        loginRequest.getPassword()
                )
        );
       
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, Errors errors) {
    	    	
    	if(userRepository.existsByName(signUpRequest.getName())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }


        if (errors.hasErrors()) {
            return new ResponseEntity(new ApiResponse(false, errors.getAllErrors().get(0).getDefaultMessage()), HttpStatus.BAD_REQUEST);
        }
    	 
        User user = new User(signUpRequest.getName(), signUpRequest.getPassword(), signUpRequest.getEmail());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER");
         //       .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));
        
        User result = userRepository.save(user);
        
        UserDetails userDetails = new UserDetails();
        userDetails.setBirthday("");
        userDetails.setCity("");
        userDetails.setCountry("");
        userDetails.setOccupation("");
        userDetails.setUser(result);
        userDetailsRepository.save(userDetails);
        

        VerificationToken vt = new VerificationToken();
        vt.setUser(user);
        String token = UUID.randomUUID().toString();
        GenerateVerificationTokenData(vt, token);
        
        VerificationToken resultToken = verificationTokenRepository.save(vt);
        
        String link = Constants.frontEndUrl+"verification?tokenId="+resultToken.getId()+"&token=" + token;        
        
        mail.sendSimpleMessage(user.getEmail(), "Programondo Activation", "Hi thank you for creating account in Programondo to activate your account please click on this link: " + link);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/user/{username}")
                .buildAndExpand(result.getName()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
    
    @PostMapping("/verification")
    public ResponseEntity<?> tokenVaildation(@RequestBody TokenRequest tokenRequest, Errors errors) {
    	
    	
    	System.out.println(tokenRequest.getTokenId());

    	System.out.println(tokenRequest.getToken());
    	
    	VerificationToken vt = verificationTokenRepository.findVerificationTokenById(tokenRequest.getTokenId());
    	
    	if(vt==null) {
    		return new ResponseEntity(new ApiResponse(false, "Token not found"),
                HttpStatus.BAD_REQUEST);
    	}
    	
    	Instant now = Instant.now();
    
    	
    	if(now.isAfter(vt.getExpiryDate())) {
    		return new ResponseEntity(new ApiResponse(false, "Token expired, please generate new."),
                    HttpStatus.BAD_REQUEST);
    	}
    	if(!vt.getToken().equals(tokenRequest.getToken())) {
    		return new ResponseEntity(new ApiResponse(false, "Token dose not match with database token. Plaese try again or contact administrator."),
                    HttpStatus.BAD_REQUEST);
    	}
    	
    	vt.getUser().setActive(true);
    	
    	userRepository.save(vt.getUser());
    	
    	return new ResponseEntity(new ApiResponse(true, "Account verified sucessfuly. Thank you, you can now login"),
                HttpStatus.OK);
    }
    
    @PostMapping("/resetVerification")
    public ResponseEntity<?> resetVerification(@RequestParam("email") String email) {
    	
    	
    	System.out.println(email);
    	
    	VerificationToken vt = verificationTokenRepository.findVerificationTokenByUserEmail(email);

    	if(vt==null) {
    		return new ResponseEntity(new ApiResponse(false, "Email not found"),
                HttpStatus.BAD_REQUEST);
    	}
    	
    	System.out.println(vt.getUser().getEmail());
    	
        String token = UUID.randomUUID().toString();
        GenerateVerificationTokenData(vt, token);
         
        VerificationToken resultToken = verificationTokenRepository.save(vt);
          
        String link =  Constants.frontEndUrl+"verification?tokenId="+resultToken.getId()+"&token=" + token;        
          
        mail.sendSimpleMessage(vt.getUser().getEmail(), "Programondo Activation", "Hi You required a new verification token to activate your account please click on this link: " + link);   	
    	
    	return new ResponseEntity(new ApiResponse(true, "New token generated please check your email"),
                HttpStatus.OK);
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
    	
    	System.out.println(email);
    	
    	PasswordToken pt = passwordTokenRepository.findPasswordTokenByUserEmail(email);

    	if(pt==null) {
    		
    		User user = userRepository.findByEmail(email);
    		
    		if(user==null) {
    		
    		return new ResponseEntity(new ApiResponse(false, "User not found"),
                HttpStatus.BAD_REQUEST);
    		}
    		else {
    			pt = new PasswordToken();
    			pt.setUser(user);
    		}
    	}
    	
        String token = UUID.randomUUID().toString();
        GeneratePasswordTokenData(pt, token);
         
        PasswordToken resultToken =  passwordTokenRepository.save(pt);
          
        String link =  Constants.frontEndUrl+"changePassword?tokenId="+resultToken.getId()+"&token=" + token;        
          
        mail.sendSimpleMessage(pt.getUser().getEmail(), "Programondo Password Reset", "Hi You required a new password to your account. Please click on this link: " + link);   	
    	
        return new ResponseEntity(new ApiResponse(true, "Password request generated please check your email"),
                HttpStatus.OK);
    }
    
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest newPassword) {
		
    	PasswordToken pt = passwordTokenRepository.findPasswordTokenById(newPassword.getTokenId());
    	
    	if(pt == null) {
    		return new ResponseEntity(new ApiResponse(true, "Token error, please generate new."),
                    HttpStatus.BAD_REQUEST);
    	}
    	
    	if(Instant.now().isAfter(pt.getExpiryDate())) {
    		 return new ResponseEntity(new ApiResponse(true, "Token expired, please generate new."),
                     HttpStatus.BAD_REQUEST);
    	}
    	
    	User user = pt.getUser();
      
    	user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
    	
    	user.setUpdatedAt(Instant.now());
    	
    	userRepository.save(user);
    		
    	 return new ResponseEntity(new ApiResponse(true, "Password changed"),
                 HttpStatus.OK);
    	
    }
    
    
    public void GenerateVerificationTokenData(VerificationToken vt, String token){
    	
        vt.setToken(token);
        Instant time = Instant.now();
    	time = time.plus(60, ChronoUnit.MINUTES);
        vt.setExpiryDate(time);
    }
    public void GeneratePasswordTokenData(PasswordToken pt, String token){
    	
        pt.setToken(token);
        Instant time = Instant.now();
    	time = time.plus(60, ChronoUnit.MINUTES);
        pt.setExpiryDate(time);
    }
}
