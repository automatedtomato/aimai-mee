package com.automatedtomato.aimaimee.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.automatedtomato.aimaimee.model.User;
import com.automatedtomato.aimaimee.service.UserService;
import com.automatedtomato.aimaimee.util.LoginAttemptTracker;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;
    Map<String, LoginAttemptTracker> loginTrackers = new HashMap<>();

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }
    
    @PostMapping("/login")
    public String processLogin(
        @RequestParam String username,
        @RequestParam String password,
        Model model,
        HttpSession session) {
    
        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Username and password are required");
            return "login";
        }
        
        LoginAttemptTracker tracker = loginTrackers.computeIfAbsent(username, k -> new LoginAttemptTracker());
    
        // Check if blocked first
        if (tracker.isAccountBlocked()) {
            model.addAttribute("error", "Account is locked. Try again later");
            return "login";
        }
        
        // Check credentials using UserService
        boolean loginSuccessful = userService.authenticateUser(username, password);
        User user = null;
        
        if (loginSuccessful) {
            user = userService.findByUsername(username);
            // Store user info in session
            session.setAttribute("username", username);
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("isAdmin", user.isAdmin());
            
            tracker.resetAttempts();    // Reset attempts
            return "redirect:/";  // Redirect on success
        
        } else {
            tracker.recordFailedAttempt();  // Record failed attempt
            
            if (tracker.isAccountBlocked()) {
                model.addAttribute("error", "Too many failed attempts. Account is locked");
            } else {
                model.addAttribute("error", "Invalid credentials. " + 
                    tracker.getRemainingAttempts() + " attempts remaining");
            }
            return "login";
        }
    }   
    
    @PostMapping("/signup")
    public String processSignup(
            @RequestParam String username, 
            @RequestParam String email, 
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {
        
        // Input validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "All fields are required");
            return "signup";
        } 
        
        // Password confirmation check
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "signup";
        }
        
        // Check if username or email already exists
        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        }
        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists");
            return "signup";
        }
        
        // Create new user
        userService.createUser(username, email, password);
        
        model.addAttribute("success", "Registration successful! Please login");
        return "redirect:/login";    // Redirect to login page
    }
    
    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        return logout(session);
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    public void clearLoginTrackers() {
        loginTrackers.clear();
    }
}