package com.healthypackageapp.service;

import com.healthypackageapp.model.Role;
import com.healthypackageapp.model.User;
import com.healthypackageapp.repository.RoleRepository;
import com.healthypackageapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    // New user registration
    @Transactional
    public User registerUser(String username, String password, String email, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    // Find user by username
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Edit user data
    @Transactional
    public User updateUser(Long id, String username, String password, String email, Set<Role> roles) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    // Delete User
    @Transactional
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

    // Add role for user
    @Transactional
    public User addRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

        if(user.getRoles().contains(role)) {
            return user;
        }

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    // Find user by id
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
