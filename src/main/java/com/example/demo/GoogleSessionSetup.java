package com.example.demo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@RequiredArgsConstructor
@ControllerAdvice
public class GoogleSessionSetup {

    @Autowired
	private  CustomerRepository customerRepo;

    @ModelAttribute
    public void addUserToSession(@AuthenticationPrincipal OAuth2User oauthUser, HttpSession session) {
        if (oauthUser != null && session.getAttribute("loggedInUser") == null) {
            String email = oauthUser.getAttribute("email");
            if (email != null) {
                Customer customer = customerRepo.findByEmail(email).orElse(null);
                if (customer != null) {
                    session.setAttribute("loggedInUser", customer);
                }
            }
        }
    }
}