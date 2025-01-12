package com.automatedtomato.aimaimee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@GetMapping("/")
	public String homePage(HttpSession session, Model model) {
		
		if (Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
			return "index";
		} else {
			model.addAttribute("error", "You are not logged in");
			return "redirect:/login";
		}
	}
}