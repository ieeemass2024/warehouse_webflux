package com.example.warehouse.handler;

import com.example.warehouse.bean.LoginRequest;
import com.example.warehouse.bean.User;
import com.example.warehouse.server.UserService;
import com.example.warehouse.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@SpringBootTest
@AutoConfigureWebTestClient
public class UserHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private ReactiveRedisOperations<String, Object> redisOperations;

    private User user;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("user1");
        user.setPassword("pass1234");
        user.setRole("USER");

        jwtToken = "fake-jwt-token";
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void testGetUsers() {
        when(userService.userList()).thenReturn(Mono.just(user).flux());

        webTestClient.mutateWith(csrf())
                .get().uri("/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                // .expectStatus().isOk()
                .expectBodyList(User.class).hasSize(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateUser() {
        when(userService.save(any(User.class))).thenReturn(Mono.just(user));

        webTestClient.mutateWith(csrf())
                .post().uri("/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .bodyValue(user)
                .exchange();
                // .expectStatus().isCreated()
                // .expectBody(String.class).isEqualTo("User created successfully");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUser() {
        when(userService.delete(any(Integer.class))).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .delete().uri("/users/{id}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .exchange();
                // .expectStatus().isOk()
                // .expectBody(String.class).isEqualTo("User deleted successfully");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateUser() {
        when(userService.update(any(User.class))).thenReturn(Mono.just(user));

        webTestClient.mutateWith(csrf())
                .put().uri("/users/{id}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .bodyValue(user)
                .exchange();
                // .expectStatus().isOk()
                // .expectBody(String.class).isEqualTo("User updated successfully");
    }

    @Test
    void testLogin() {
    LoginRequest loginRequest = new LoginRequest(jwtToken, jwtToken);
    loginRequest.setUsername("user1");
    loginRequest.setPassword("pass1234");

    when(userService.authenticate(anyString(), anyString())).thenReturn(Mono.just(user));
    when(jwtUtils.createJwt(any(), anyString(), anyString())).thenReturn(jwtToken);
    when(redisOperations.keys(anyString())).thenReturn(Flux.fromIterable(Collections.emptySet()));
    when(redisOperations.delete(anyString())).thenReturn(Mono.empty());

    webTestClient.mutateWith(csrf())
            .post().uri("/users/login")
            .bodyValue(loginRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(HashMap.class)
            .consumeWith(response -> {
                HashMap<String, Object> body = response.getResponseBody();
                assert body != null;
                assert body.containsKey("jwtToken");
                assert body.containsKey("user");
            });
}

}
