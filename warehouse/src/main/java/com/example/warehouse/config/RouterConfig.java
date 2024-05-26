package com.example.warehouse.config;

import com.example.warehouse.handler.ItemHandler;
import com.example.warehouse.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import io.github.bucket4j.Bucket;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class RouterConfig {

    private final Bucket rateLimitBucket = RateLimitConfig.createBucket();
    private final HandlerFilterFunction<ServerResponse, ServerResponse> rateLimitFilter = (request, next) -> {
        if (rateLimitBucket.tryConsume(1)) {
            return next.handle(request);
        } else {
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    };

    @Bean
    public RouterFunction<ServerResponse> itemRoutes(ItemHandler handler) {
        return RouterFunctions.route()
                .GET("/items", handler::getAllItems).filter(rateLimitFilter)
                .GET("/items/{id}", handler::getItemById).filter(rateLimitFilter)
                .POST("/items", handler::createItem).filter(rateLimitFilter)
                .PUT("/items/{id}", handler::updateItem).filter(rateLimitFilter)
                .DELETE("/items/{id}", handler::deleteItem).filter(rateLimitFilter)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return RouterFunctions.route()
                .GET("/users", handler::getUsers)
                .POST("/users", handler::createUser)
                .DELETE("/users/{id}", handler::deleteUser)
                .POST("/users/login", handler::login)
                .build();
    }



}
