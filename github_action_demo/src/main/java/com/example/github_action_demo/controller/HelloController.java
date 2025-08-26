package com.example.github_action_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello from EC2! 🚀";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}