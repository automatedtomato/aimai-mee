package com.automatedtomato.aimaimee.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.automatedtomato.aimaimee.model.User;
import com.automatedtomato.aimaimee.util.LoginAttemptTracker;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
	List<User> userList = new ArrayList<>();
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
	    
	    // Check credentials
	    boolean loginSuccessful = false;
	    for (User user : userList) {
	        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
	            loginSuccessful = true;  // Set to TRUE on success
	            break;
	        }
	    }
	    
	    if (loginSuccessful) {
	    	
	    	// Store user info in session
	    	session.setAttribute("username", username);
	    	session.setAttribute("isLoggedIn", true);
	    	
	    	for (User user : userList) {
	    		if (user.getUsername().equals(username)) {
	    			session.setAttribute("isAdmin", user.isAdmin());
	    			break;
	    		}
	    	}
	    	
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
		for (User user : userList) {
			if (user.getUsername().equals(username)) {
				model.addAttribute("error", "Username already exists");
				return "signup";
			}
			if (user.getEmail().equals(email)) {
				model.addAttribute("error", "Email already exists");
				return "signup";
			}
		}
		
		// Create new user
		User newUser = new User (username, email, password);
		userList.add(newUser);
		
		model.addAttribute("success", "Registration successful! Please login");
		return "redirect:/login";	// Redirect to login page
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

	public List<User> getUserList() {
		return userList;
	}
	
	public void clearLoginTrackers() {
	    loginTrackers.clear();
	}
	
}
