package com.automatedtomato.aimaimee.util;

import java.time.LocalDateTime;

public class LoginAttemptTracker {
	private int attempts = 0;
	private LocalDateTime lastAttemptTime;
	private boolean isBlocked;
	private LocalDateTime lockExpirationTime;
	private static final int MAX_ATTEMPTS = 3;
	private static final int LOCK_TIME = 15;	// in minutes
	
	
	public void recordFailedAttempt() {
		attempts++;
		lastAttemptTime = LocalDateTime.now();
		
		if (attempts >= MAX_ATTEMPTS) {
			setAccountBlocked();
		}
	}
	
	public boolean isAccountBlocked() {
		if (lockExpirationTime == null || !isBlocked) {
			return false;
		} 
		
		if(LocalDateTime.now().isAfter(lockExpirationTime)) {
			setAccountUnblocked();
			return false;
		}
		return true;
	}
	
	public void setAccountBlocked() {
		isBlocked = true;
		lockExpirationTime = LocalDateTime.now().plusMinutes(LOCK_TIME); 
	}
	
	public int getAttempts() {
		return attempts;
	}

	public void resetAttempts() {
		attempts = 0;
		lastAttemptTime = null;
		isBlocked = false;
		lockExpirationTime = null;
	}
	
	public int getRemainingAttempts() {
		return Math.max(0, MAX_ATTEMPTS - attempts);
	}
	
	public void setAccountUnblocked() {
		isBlocked = false;
		lockExpirationTime = null;
	}
	
	
	public LocalDateTime getLockExpirationTime() {
		return lockExpirationTime;
	}
	
}
