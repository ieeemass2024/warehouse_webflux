package com.example.warehouse.handler;

import com.example.warehouse.bean.LoginRequest;
import com.example.warehouse.bean.User;
import com.example.warehouse.server.UserService;
import com.example.warehouse.util.JwtUtils;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class UserHandler {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final ReactiveRedisOperations<String, Object> redisOperations;

    public UserHandler(UserService userService, JwtUtils jwtUtils, ReactiveRedisOperations<String, Object> redisOperations) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.redisOperations = redisOperations;
    }



    public Mono<ServerResponse> getUsers(ServerRequest request) {
        return ok().body(userService.userList(), User.class);
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);
        return userMono.flatMap(userService::save)
                .flatMap(savedUser -> ServerResponse.status(HttpStatus.CREATED).bodyValue("User created successfully"))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error creating user: " + e.getMessage()));
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        String id = request.pathVariable("id");
        return userService.delete(Integer.valueOf(id))
                .then(ServerResponse.ok().bodyValue("User deleted successfully"))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error deleting user: " + e.getMessage()));
    }



    public Mono<ServerResponse> login(ServerRequest request) {
        Mono<LoginRequest> loginRequestMono = request.bodyToMono(LoginRequest.class);
        return loginRequestMono.flatMap(loginRequest ->
                userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())
                        .flatMap(user -> {
                            return redisOperations.keys("*")
                                    .flatMap(redisOperations::delete)
                                    .then(Mono.just(user));
                        })
                        .flatMap(user -> {
                            String jwtToken = jwtUtils.createJwt(
                                    new org.springframework.security.core.userdetails.User(
                                            user.getUsername(),
                                            user.getPassword(),
                                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))),
                                    user.getId().toString(),
                                    user.getUsername());

                            HashMap<String, Object> response = new HashMap<>();
                            response.put("jwtToken", jwtToken);
                            response.put("user", user);
                            return ok().bodyValue(response);
                        })
                        .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(new HashMap<>() {
                            {
                                put("message", "Invalid credentials");
                            }
                        }))
                        .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(new HashMap<>() {
                            {
                                put("message", "Error during login: " + e.getMessage());
                            }
                        })));
    }

}
