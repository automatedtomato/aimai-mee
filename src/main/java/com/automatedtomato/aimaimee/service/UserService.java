package com.automatedtomato.aimaimee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.automatedtomato.aimaimee.model.User;
import com.automatedtomato.aimaimee.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    User user = findByUsername(username);
	    if (user == null) {
	        System.out.println("DEBUG: User not found: " + username);
	        throw new UsernameNotFoundException("User not found");
	    }
	    System.out.println("DEBUG: Found user: " + username);
	    System.out.println("DEBUG: User password hash: " + user.getPassword());
	    
	    return org.springframework.security.core.userdetails.User
	            .withUsername(user.getUsername())
	            .password(user.getPassword())
	            .roles("USER")
	            .build();
	}
	
	private final BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	public UserService(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
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
