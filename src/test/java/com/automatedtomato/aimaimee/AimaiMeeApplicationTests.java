package com.automatedtomato.aimaimee;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.automatedtomato.aimaimee.controller.AuthController;
import com.automatedtomato.aimaimee.model.User;

@SpringBootTest
@AutoConfigureMockMvc
class AimaiMeeApplicationTests {

	@Autowired
	private MockMvc mockMvc;  // To simulate HTTP requests
	
	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_PASSWORD = "password123";
	private static final String TEST_EMAIL = "test@example.com";
	
	@Autowired
	private AuthController authController;
	
	@BeforeEach
	void setUp() {
		
		authController.getUserList().clear();
		
		User testUser = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		authController.getUserList().add(testUser);
	}
	
	private void performLogin(String username, String password) throws Exception {
		mockMvc.perform(
			post("/login")
			.param("username", username)
			.param("password", password)
		);
	}
	
	@Test
	void loginPageShouldLoad() throws Exception {
		mockMvc.perform(get("/login"))  	// Send Get request to /login
		.andExpect(status().isOk())    		// Expect HTTP 200 OK response
		.andExpect(view().name("login"));	// Expect login view
	}
	
	@Test
	void loginWithValidCredentialsShouldSucceed() throws Exception {
		mockMvc.perform(
			post("/login")
			.param("username", TEST_USERNAME)
			.param("password", TEST_PASSWORD))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/")
		);
	}
	
	@Test
	void loginWithInvalidCredentialsShouldFail() throws Exception {
		authController.clearLoginTrackers();
	    mockMvc.perform(post("/login")
	            .param("username", TEST_USERNAME)
	            .param("password", "wrongpassword"))
	    
	            .andExpect(status().isOk())
	            .andExpect(view().name("login"))
	            .andExpect(model().attribute("error", containsString("Invalid credentials")));
	}
	
	@Test
	void accountShouldLockAfterThreeFailedAttempts() throws Exception {
		for (int i = 0; i < 3; i++) {
			mockMvc.perform(
				post("/login")
				.param("username", TEST_USERNAME)
				.param("password", "wrongpassword")
			)
			.andExpect(status().isOk())
			.andExpect(view().name("login"));
		}
		
		mockMvc.perform(
			post("/login")
			.param("username", TEST_USERNAME)
			.param("password", TEST_PASSWORD)
		)
		.andExpect(status().isOk())
		.andExpect(view().name("login"))
		.andExpect(model().attribute("error",
				org.hamcrest.Matchers.containsString("Account is locked. Try again later")));
	}
	
	@Test
	void lockedAccountShouldNotBeAbleToLogin() throws Exception {
		for (int i = 0; i < 3; i++) {
			performLogin(TEST_USERNAME, "wrongpassword"); 
		}
		
		mockMvc.perform(
			post("/login")
			.param("username", TEST_USERNAME)
			.param("password", TEST_PASSWORD)
		)
		.andExpect(status().isOk())
		.andExpect(view().name("login"))
		.andExpect(model().attribute("error", "Account is locked. Try again later"));
	}
	
	@Test
	void protectedPagesShouldRequireLogin() throws Exception {
		mockMvc.perform(get("/"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/login"));
	}
	
	@Test
	void logoutShouldInvalidateSession() throws Exception {
	    MockHttpSession session = new MockHttpSession();
	    
	    session.setAttribute("isLoggedIn", true);
	    session.setAttribute("username", TEST_USERNAME);
	    
		mockMvc.perform(
				post("/logout")
				.session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}
	
	@Test
	void sessionShouldPersisitAfterLogin() throws Exception {
	    // First do successful login
		mockMvc.perform(post("/login")
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

	    // Then try accessing protected page
		mockMvc.perform(get("/")
				.sessionAttr("isLoggedIn", true)
				.sessionAttr("username", TEST_USERNAME))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
	
}
