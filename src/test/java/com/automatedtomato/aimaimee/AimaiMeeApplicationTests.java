package com.automatedtomato.aimaimee;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
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
import com.automatedtomato.aimaimee.repository.UserRepository;
import com.automatedtomato.aimaimee.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class AimaiMeeApplicationTests {

    @Autowired
    private MockMvc mockMvc;  // To simulate HTTP requests
    
    @Autowired
	private UserService userService;
    
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_USERNAME2 = "testuser2";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_EMAIL2 = "test2@example.com";
    
    @Autowired
    private AuthController authController;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userService.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
    }
    
    private void performLogin(String username, String password) throws Exception {
        mockMvc.perform(
            post("/login")
            .param("username", username)
            .param("password", password)
        );
    }
    
    // Original Authentication Tests
    @Test
    void loginPageShouldLoad() throws Exception {
        mockMvc.perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(view().name("login"));
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
    void sessionShouldPersistAfterLogin() throws Exception {
        // Clear previous state
        authController.clearLoginTrackers();
        
        // Perform login
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
    
    // New Database-specific Tests
    @Test
    void signupShouldCreateNewUser() throws Exception {
        String newUsername = "newuser";
        String newEmail = "new@example.com";
        String newPassword = "newpass123";
        
        mockMvc.perform(post("/signup")
            .param("username", newUsername)
            .param("email", newEmail)
            .param("password", newPassword)
            .param("confirmPassword", newPassword))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
            
        // Verify user was created in database
        User createdUser =  userService.createUser(newUsername, newEmail, newPassword);;
        assertNotNull(createdUser);
        assertEquals(newUsername, createdUser.getUsername());
        assertEquals(newEmail, createdUser.getEmail());
        assertEquals(newPassword, createdUser.getPassword());
    }
    
    @Test
    void signupShouldPreventDuplicateUsername() throws Exception {
        mockMvc.perform(post("/signup")
            .param("username", TEST_USERNAME)  // Using existing username
            .param("email", "another@example.com")
            .param("password", "pass123")
            .param("confirmPassword", "pass123"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("error", "Username already exists"));
            
        // Verify only one user exists with this username
        assertEquals(1, userRepository.findByUsernameContaining(TEST_USERNAME).size());
    }
    
    @Test
    void signupShouldPreventDuplicateEmail() throws Exception {
        mockMvc.perform(post("/signup")
            .param("username", "anotheruser")
            .param("email", TEST_EMAIL)  // Using existing email
            .param("password", "pass123")
            .param("confirmPassword", "pass123"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("error", "Email already exists"));
            
        // Verify only one user exists with this email
        assertEquals(1, userRepository.findByEmailContaining(TEST_EMAIL).size());
    }
    
    @Test
    void userDataShouldPersistBetweenRequests() throws Exception {
        // First, create a new user
        String newUsername = "persistencetest";
        String newEmail = "persist@example.com";
        String newPassword = "persist123";
        
        mockMvc.perform(post("/signup")
            .param("username", newUsername)
            .param("email", newEmail)
            .param("password", newPassword)
            .param("confirmPassword", newPassword));
            
        // Then try to log in with the created user
        mockMvc.perform(post("/login")
            .param("username", newUsername)
            .param("password", newPassword))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }
    
    @Test
    void registrationShouldSucceed() throws Exception {
    	mockMvc.perform(post("/signup")
				.param("username", TEST_USERNAME2)
				.param("email", TEST_EMAIL2)
				.param("password", TEST_PASSWORD)
				.param("confirmPassword", TEST_PASSWORD))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
    	
    	User savedUser = userService.createUser(TEST_USERNAME2, TEST_EMAIL2, TEST_PASSWORD);
		assertNotNull(savedUser);
		assertEquals(TEST_EMAIL2, savedUser.getEmail());
		assertEquals(TEST_USERNAME2, savedUser.getUsername());
		assertEquals(TEST_PASSWORD, savedUser.getPassword());
		
    }
    
}