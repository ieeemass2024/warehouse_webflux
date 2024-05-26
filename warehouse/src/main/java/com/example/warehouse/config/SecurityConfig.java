// package com.example.warehouse.config;

// import com.example.warehouse.bean.User;
// import com.example.warehouse.filter.JwtAuthorizeFilter;
// import com.example.warehouse.util.JwtUtils;
// import jakarta.annotation.Resource;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.security.core.context.SecurityContextHolder;

// import java.io.IOException;
// import java.io.PrintWriter;

// @SuppressWarnings("deprecation")
// @Configuration
// @EnableMethodSecurity
// @EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法级安全性
// public class SecurityConfiguration {

//     @Resource
//     private JwtUtils jwtUtils;

//     @Resource
//     private JwtAuthorizeFilter jwtAuthorizeFilter;

//     @Resource
//     private UserDetailsService userDetailsService;  // Assuming you are using Spring Security's user details service

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         return http
//                 .authorizeHttpRequests(auth -> {
//                     auth.requestMatchers("/users/login").permitAll(); // 登录
//                     auth.requestMatchers("/items/**").hasAnyRole("USER", "ADMIN");


//                     // auth.requestMatchers("/users/logout").permitAll(); // 注销
//                     // auth.requestMatchers("/items/**").hasRole("USER"); // 用户相关操作
//                     // auth.requestMatchers("/items/**").hasRole("ADMIN"); // 管理员操作
//                     auth.anyRequest().authenticated();
//                 })
//                 // .formLogin(conf -> {
//                 //     conf.loginProcessingUrl("/users/login")
//                 //         .failureHandler(this::authenticationFailure)
//                 //         .successHandler(this::authenticationSuccess)
//                 //         .permitAll();
//                 // })
//                 .logout(conf -> {
//                     conf.logoutUrl("/users/logout")
//                             .logoutSuccessHandler(this::logoutSuccess);
//                 })
//                 .exceptionHandling(conf -> conf
//                         .authenticationEntryPoint(this::unauthorized)
//                         .accessDeniedHandler(this::accessDenied))
//                 .csrf(conf -> conf.disable())
//                 .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                 .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
//                 .build();
//     }

//     private void accessDenied(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
//         response.setContentType("application/json;charset=utf-8");
//         response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"" + e.getMessage() + "\"}");
//     }

//     private void unauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
//         response.setContentType("application/json;charset=utf-8");
//         response.getWriter().write("{\"status\":401,\"error\":\"前端：Unauthorized\",\"message\":\"" + e.getMessage() + "\"}");
//     }

//     private void logoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//         response.setContentType("application/json;charset=utf-8");
//         PrintWriter writer = response.getWriter();
//         String authorization = request.getHeader("Authorization");
//         if (jwtUtils.invalidateJwt(authorization)) {
//             writer.write("{\"status\":200,\"message\":\"Successfully logged out\"}");
//         } else {
//             writer.write("{\"status\":400,\"error\":\"Failed to log out\",\"message\":\"Failed to log out\"}");
//         }
//     }

//     private void authenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//         UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//         User user = (User) userDetailsService.loadUserByUsername(userDetails.getUsername());

//         String jwtToken = jwtUtils.createJwt(userDetails, user.getId().toString(), user.getUsername());

//         response.setContentType("application/json;charset=utf-8");
//         response.getWriter().write("{\"status\":200,\"test：token\":\"" + jwtToken + "\",\"username\":\"" + user.getUsername() + "\",\"role\":\"" + user.getRole() + "\"}");
//     }

//     private void authenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
//         response.setContentType("application/json;charset=utf-8");
//         response.getWriter().write("{\"status\":401,\"error\":\"标记：Unauthorized\",\"message\":\"" + e.getMessage() + "\"}");
//     }
// }
package com.example.warehouse.config;

import com.example.warehouse.filter.JwtAuthorizeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthorizeFilter jwtAuthorizeFilter;

    public SecurityConfig(JwtAuthorizeFilter jwtAuthorizeFilter) {
        this.jwtAuthorizeFilter = jwtAuthorizeFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(auth -> auth
                        .pathMatchers("/users/login", "/users/register").permitAll()
                        .pathMatchers("/items/**").hasAnyRole("USER", "ADMIN")
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        })
                )
                .addFilterAt(jwtAuthorizeFilter, org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }


}
