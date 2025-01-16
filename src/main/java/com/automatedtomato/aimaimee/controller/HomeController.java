package com.automatedtomato.aimaimee.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@GetMapping("/")
	public String homePage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null && authentication.isAuthenticated()){
			return "index";
		} else {
			model.addAttribute("error", "You are not logged in");
			return "redirect:/login";
		}
	}
}