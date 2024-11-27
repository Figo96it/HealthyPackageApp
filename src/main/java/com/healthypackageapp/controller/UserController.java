package com.healthypackageapp.controller;

import com.healthypackageapp.model.User;
import com.healthypackageapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint for editing user data - accessible only to the user themselves (not admins)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update")
    public ResponseEntity<User> update(@RequestBody User updatedUser) {
        // Get the currently logged-in user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Check if the user wants to edit their own data
        if (!currentUsername.equals(updatedUser.getUsername())) {
            return ResponseEntity.status(403).body(null);
        }

        // Check if the username already exists in the database
        Optional<User> existingUser = userService.findUserByUsername(updatedUser.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(updatedUser.getId())) {
            return ResponseEntity.badRequest().body(null); // Conflict, username already taken
        }


        // Update the user
        User user = userService.updateUser(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getPassword(), updatedUser.getEmail(), updatedUser.getRoles());
        return ResponseEntity.ok(user);
    }

    // Endpoint for deleting a user - accessible only to admins
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Check if user exists
        Optional<User> user = userService.findById(id);

        // If user does not exists, return 404 not found
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Delete user
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint for adding roles to a user - accessible only to admins
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<User> addRole(@PathVariable Long userId, @PathVariable Long roleId) {
        // Check if user exists
        Optional<User> user = userService.findById(userId);

        // If user does not exists, return 404 not found
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User userWithNewRole = userService.addRoleToUser(userId, roleId);
        return ResponseEntity.ok(userWithNewRole);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username){
        return userService.findUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
