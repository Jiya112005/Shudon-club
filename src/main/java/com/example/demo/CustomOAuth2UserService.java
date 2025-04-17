package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        String email = user.getAttribute("email");
        String name = user.getAttribute("name");

        Customer customer = customerRepository.findByEmail(email).orElse(null);
        if (customer == null) {
            // User doesn't exist → register
            customer = new Customer();
            customer.setFirstName(name);
            customer.setEmail(email);
            customer.setPassword("GOOGLE"); // mark it as Google auth
            customer.setAge(0); // default age, update later
            customerRepository.save(customer);
        }

        return user;
    }
}