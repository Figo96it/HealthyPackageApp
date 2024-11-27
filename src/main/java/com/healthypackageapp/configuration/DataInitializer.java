package com.healthypackageapp.configuration;

import com.healthypackageapp.model.Role;
import com.healthypackageapp.model.RoleName;
import com.healthypackageapp.model.User;
import com.healthypackageapp.repository.RoleRepository;
import com.healthypackageapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * This class is intended for initializing test data in development or testing environments only.
 * It should not be used in production environments.
 */
@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeData() {
        //Add roles
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);
        roleRepository.save(userRole);

        Role adminRole = new Role();
        adminRole.setName(RoleName.ROLE_ADMIN);
        roleRepository.save(adminRole);

        //Add users
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        System.out.println("Test data initialized.");

    }
}
