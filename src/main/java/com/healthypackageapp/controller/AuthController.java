package com.healthypackageapp.controller;

import com.healthypackageapp.model.User;
import com.healthypackageapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Zalogowano pomyślnie");
    }

    // Endpoint for user registration - accessible to everyone
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        // User registration logic, assuming the frontend sends the complete User object
        User newUser = userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getRoles());
        return ResponseEntity.ok(newUser);
    }
}
