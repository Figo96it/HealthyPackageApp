package com.healthypackageapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthypackageapp.model.Role;
import com.healthypackageapp.model.RoleName;
import com.healthypackageapp.model.User;
import com.healthypackageapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("password");
        user.setEmail("john@gmail.com");
        user.setRoles(new HashSet<>(Set.of(role)));

        Mockito.when(userService.registerUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anySet())).thenReturn(user);
        Mockito.when(userService.findUserByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anySet())).thenReturn(user);
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userService.addRoleToUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(user);
    }

    @Test
    @WithMockUser(username = "john", roles = {"USER"})
    @DirtiesContext
    public void testUpdateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));

        Mockito.verify(userService, Mockito.times(1)).updateUser(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anySet());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DirtiesContext
    public void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(Mockito.anyLong());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DirtiesContext
    public void testAddRoleToUser() throws Exception {
        // Przygotowanie nowej roli
        Role newRole = new Role();
        newRole.setId(2L);
        newRole.setName(RoleName.ROLE_ADMIN);

        user.getRoles().add(newRole);

        Mockito.when(userService.addRoleToUser(Mockito.anyLong(), Mockito.anyLong())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/1/roles/2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.roles[?(@.id == 2)].name").value("ROLE_ADMIN")); // Sprawdzenie nowej roli w odpowiedzi

        Mockito.verify(userService, Mockito.times(1)).addRoleToUser(Mockito.eq(1L), Mockito.eq(2L));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DirtiesContext
    public void testUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"))
                .andExpect(jsonPath("$.roles[?(@.id == 1)].name").value("ROLE_USER"));

        Mockito.verify(userService, Mockito.times(1)).findById(Mockito.eq(1L));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DirtiesContext
    public void testUserByUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/username/john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"))
                .andExpect(jsonPath("$.roles[?(@.id == 1)].name").value("ROLE_USER"));

        Mockito.verify(userService, Mockito.times(1)).findUserByUsername(Mockito.eq("john"));

    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }
}