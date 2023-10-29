package com.yourpackage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import com.yourpackage.model.AuthResponse;
import com.yourpackage.model.AuthRequest;
import com.yourpackage.service.AuthService;

@Controller
public class AuthController {
    private final AuthService authService;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthController(AuthService authService, RestTemplate restTemplate) {
        this.authService = authService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Return the login.html template
    }

    @PostMapping("/login")
    public String authenticateUser(Model model, String login_id, String password) {
        // Set headers for the authentication API request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request entity with login_id and password
        HttpEntity<AuthRequest> requestEntity = new HttpEntity<>(new AuthRequest(login_id, password), headers);

        // Make a POST request to the authentication API to get the Bearer token
        String authUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";
        AuthResponse authResponse = restTemplate.postForObject(authUrl, requestEntity, AuthResponse.class);

        if (authResponse != null && authResponse.getToken() != null) {
            // Store the Bearer token in AuthService upon successful login
            authService.setBearerToken(authResponse.getToken());
            return "redirect:/customer-list"; // Redirect to customer list page
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login"; // Return to the login page with an error message
        }
    }
}
