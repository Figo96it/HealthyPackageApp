package com.healthypackageapp.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DevSecurityConfigurationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testPublicEndpoint(){
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/auth/login")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testProtectedEndpointUnauthorized(){
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/protected")
                .exchange()
                .expectStatus().isUnauthorized();

    }
}
