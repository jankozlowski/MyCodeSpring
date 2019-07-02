package com.binaryalchemist.programondo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
public class ReactController {

    @RequestMapping(value = {"/", "/login", "/registration", "/verification", "/resetValidation", "/resetPassword", "/changePassword"})
    public String index() {
        return "index.html";
    }
}