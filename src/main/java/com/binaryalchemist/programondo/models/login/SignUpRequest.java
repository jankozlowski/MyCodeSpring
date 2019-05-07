package com.binaryalchemist.programondo.models.login;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SignUpRequest {

	@NotBlank
    private String name;

	@NotBlank
    @Email(message = "Incorrect email format")
    private String email;

    @NotBlank
    private String password;
    
}
