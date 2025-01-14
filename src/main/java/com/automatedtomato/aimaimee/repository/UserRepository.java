package com.automatedtomato.aimaimee.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automatedtomato.aimaimee.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	
	List<User> findByCreatedAtAfter(LocalDateTime createdAt);
	int countByIsAdmin(boolean isAdmin);
	List<User> findByEmailContaining(String domain);
	List<User> findByUsernameContaining(String usernamePart);
	User findByEmail(String email);
}
