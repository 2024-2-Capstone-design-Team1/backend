package com.project.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping
    public String health(){
        return "ok";
    }

    @GetMapping("/health")
    public String health2(){
        return "ok";
    }
}
