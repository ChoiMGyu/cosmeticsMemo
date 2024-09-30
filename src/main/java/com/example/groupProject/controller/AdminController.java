package com.example.groupProject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin")
    public String adminP() {
        System.out.println("admin controller 접근");
        return "admin Controller";
    }
}
