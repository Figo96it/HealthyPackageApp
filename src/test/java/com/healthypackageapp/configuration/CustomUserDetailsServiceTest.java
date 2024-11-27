package com.healthypackageapp.configuration;

import com.healthypackageapp.model.Role;
import com.healthypackageapp.model.RoleName;
import com.healthypackageapp.model.User;
import com.healthypackageapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    @Test
    void loadUserByUsernameWhenUserExists() {
        //UserRepository mock
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);

        //Create instance of CustomUserDetailsService
        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(mockUserRepository);

        //Create Role
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);

        //Create User
        User user = new User();
        user.setUsername("user");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));
        user.setRoles(Set.of(userRole));

        // We configure the mock to return the user
        when(mockUserRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // We check if the user will be loaded correctly
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user");

        assertNotNull(userDetails);
        assertEquals("user", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsernameWhenUserNotFound(){
        //UserRepository Mock
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);

        //Create instance of CustomUserDetailsService
        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(mockUserRepository);

        // We configure the mock to return Optional.empty() - no user
        when(mockUserRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // We check if the UsernameNotFoundException is thrown
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("nonexistent"));
    }
}
