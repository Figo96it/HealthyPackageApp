package com.healthypackageapp.service;

import com.healthypackageapp.model.Role;
import com.healthypackageapp.model.RoleName;
import com.healthypackageapp.model.User;
import com.healthypackageapp.repository.RoleRepository;
import com.healthypackageapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role roleUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create example data
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName(RoleName.ROLE_USER);

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("password");
        user.setEmail("john@example.com");
        user.setRoles(Set.of(roleUser));
    }

    @Test
    public void testAddRoleToUser() throws Exception {
        //Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(roleUser));
        when(userRepository.save(user)).thenReturn(user);

        //Act
        User updatedUser = userService.addRoleToUser(1L, 1L);

        //Assert
        assertNotNull(updatedUser);
        assertTrue(updatedUser.getRoles().contains(roleUser));
    }

    @Test
    public void testAddRoleToUserWhenRoleAlreadyAssigned() throws Exception {
        //Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenReturn(user);

        //User already has the role, so the role should not be added again
        User updatedUser = userService.addRoleToUser(1L, 1L);

        //Asserts
        assertNotNull(updatedUser);
        assertEquals(1, updatedUser.getRoles().size()); // Role should not be duplicated
    }

    @Test
    public void testDeleteUser(){
        //Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        //Act
        userService.deleteUser(1L);

        //Asserts
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserNotFound(){
        //Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act & Asserts
        assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
    }
}
