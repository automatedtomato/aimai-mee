package com.automatedtomato.aimaimee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.automatedtomato.aimaimee.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private UserService userService;
	
	
	private final PasswordConfig passwordConfig;
	
	@Autowired
	public SecurityConfig(UserService userService, PasswordConfig passwordConfig) {
		this.userService = userService;
		this.passwordConfig = passwordConfig;
	}
	
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
	    auth.setUserDetailsService(userService);
	    auth.setPasswordEncoder(passwordConfig.passwordEncoder());
	    return auth;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(Customizer.withDefaults())
	        .authenticationProvider(authenticationProvider())  // Add this line
	        .authorizeHttpRequests(auth ->
	            auth
	                .requestMatchers("/login", "/signup", "/css/**").permitAll()
	                .anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
				.permitAll()
			);
		return http.build();
	}

}
