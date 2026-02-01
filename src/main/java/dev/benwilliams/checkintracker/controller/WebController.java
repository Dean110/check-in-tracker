package dev.benwilliams.checkintracker.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/dashboard")
    @PreAuthorize("@userRepository.findByEmail(authentication.name).isPresent()")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/admin/users")
    @PreAuthorize("@adminRepository.findByEmail(authentication.name).isPresent()")
    public String adminUsers() {
        return "admin/users";
    }
}
