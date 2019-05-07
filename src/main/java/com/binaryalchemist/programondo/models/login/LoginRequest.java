package com.binaryalchemist.programondo.models.login;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String password;

}