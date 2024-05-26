package com.example.warehouse.filter;

import com.example.warehouse.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthorizeFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizeFilter.class);

    private final JwtUtils jwtUtils;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthorizeFilter(JwtUtils jwtUtils, ReactiveUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();



        if (path.startsWith("/users/login") || path.startsWith("/users/register") ) {
            return chain.filter(exchange);
        }


        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");

        return jwtUtils.resolveJWT(authorization)
                .flatMap(decodedJWT -> {
                    String username = decodedJWT.getSubject();
                    if (username == null) {
                        logger.error("Username is null in JWT");
                        return Mono.error(new IllegalStateException("Username is null in JWT"));
                    }
                    logger.info("Resolved username: {}", username);
                    return userDetailsService.findByUsername(username)
                            .flatMap(userDetails -> {
                                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                SecurityContext context = new SecurityContextImpl(authenticationToken);
                                logger.info("User {} authenticated with authorities {}", username, userDetails.getAuthorities());
                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                logger.error("User not found: {}", username);
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().setComplete();
                            }));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Unauthorized access attempt to {}", exchange.getRequest().getURI());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }));
    }
}
