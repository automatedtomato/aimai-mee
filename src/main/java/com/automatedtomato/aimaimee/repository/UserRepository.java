package com.automatedtomato.aimaimee.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automatedtomato.aimaimee.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByUsername(String username);
	List<User> findByUsernameContaining(String usernamePart);
	boolean existsByUsername(String username);
	
	User findByEmail(String email);
	List<User> findByEmailContaining(String domain);
	boolean existsByEmail(String email);
	
	List<User> findByCreatedAtAfter(LocalDateTime createdAt);
	
	int countByIsAdmin(boolean isAdmin);
}
