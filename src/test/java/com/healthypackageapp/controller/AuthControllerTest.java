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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

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
    }

    @Test
    public void testRegisterUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));

        Mockito.verify(userService, Mockito.times(1)).registerUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anySet());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }
}
