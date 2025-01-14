package com.automatedtomato.aimaimee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.automatedtomato.aimaimee.model.User;
import com.automatedtomato.aimaimee.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	public User createUser(String userName, String email, String rawPassword) {
		User user = new User(userName, email, passwordEncoder.encode(rawPassword));
		return userRepository.save(user);
	}
	
	public boolean verifyPassword(String rawPassword, String hashedPassword) {
		return passwordEncoder.matches(rawPassword, hashedPassword);
	}
	
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public boolean authenticateUser(String username, String rawPassword) {
		User user = findByUsername(username);
		return user != null && passwordEncoder.matches(rawPassword, user.getPassword());
	}
	
}
