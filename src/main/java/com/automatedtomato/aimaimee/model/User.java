package com.automatedtomato.aimaimee.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

//TODO setPassword method creation is pending

@Entity
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generated ID values
	private long id;
	
	@Column(name="username")
	private String username;
	
	@Column(name="email")
	private String email;
	
	@Column(name="password")
	private String password;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="is_admin")
	private boolean isAdmin;

	public User() {
		this.createdAt = LocalDateTime.now();
		this.isAdmin = false;
	}
	
	public User(String username, String email, String password) {
		this();
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void promoteAdmin() {
		this.isAdmin = true;
	}
	
}
