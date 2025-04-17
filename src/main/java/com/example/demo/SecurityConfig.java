package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	 @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers(
	                		  "/uploads/**",              // 👈 allow all images/videos here
	                		    "/css/**", "/js/**", "/images/**",
	                		    "/shudonlogo.png", "/favicon3.png", "/video2Home.mp4",
	                		    "/auth/**", "/admin/**",
	                		    "/", "/home", "/auth/register", "/auth/userlogin", "/error",
	                		    "/passes/**", "/api/bookings/**", "/auth/invitations/**",
	                		    "/download-receipt", "/payment/**", "/auth/friends/**",
	                		    "/submit-feedback","/gamenight2.jpeg","/booking.jpg","/gamenight3.jpeg","/gamenight4.jpeg","/gamenight1.jpeg",
	                		    "/party.jpg","/image1Home.png","/shudonlogo.jpg-removebg.png","/recommendedgames1.jpeg","/recommededgames2.jpg",
	                		    "/recommendedgames3.png","/recommendedgames4.png","/recommendedgames5.jpg","/recommededgames6.jpg","/recommendedgames7.png",
	                		    "/recommendedgames8.jpg"
	                ).permitAll()
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                .loginPage("/auth/userlogin")
	                .loginProcessingUrl("/spring-login")
	                .permitAll()
	            )
	            .oauth2Login(oauth -> oauth
	                .loginPage("/auth/userlogin")
	                .defaultSuccessUrl("/auth/home", true) 
	            )
	            .logout(logout -> logout
	                .logoutUrl("/auth/logout")
	                .logoutSuccessUrl("/auth/userlogin?logout")
	                .permitAll()
	            )
	            .csrf(csrf -> csrf
	            		.ignoringRequestMatchers("/auth/**", "/admin/**","/confirmBooking","/auth/book-session",  "/payment/**","/submit-feedback","/api/bookings/**","/auth/customers/**","/auth/friends/**","/passes") // if needed
	            );

	        return http.build();
	    }

}
