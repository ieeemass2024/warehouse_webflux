package com.example.warehouse.server;

import com.example.warehouse.mapper.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomReactiveUserDetailsService.class);
    private final UserRepository userRepository;

    public CustomReactiveUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        logger.info("Looking for user: {}", username);

        return userRepository.findByUsername(username)
                .map(user -> {
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
                    return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                            .password("{noop}" + user.getPassword()) // 假设密码不加密
                            .authorities(authorities)
                            .build();
                })
                .doOnNext(userDetails -> logger.info("User found: {}", username))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("User not found: {}", username);
                    return Mono.empty();
                }));
    }
}

// package com.example.warehouse.server;

// import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;
// import reactor.core.publisher.Mono;

// @Service
// public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

//     @Override
//     public Mono<UserDetails> findByUsername(String username) {
//         // 模拟从数据库中查找用户信息
//         if ("user1".equals(username)) {
//             return Mono.just(User.withUsername(username)
//                     .password("{noop}password")
//                     .authorities("ROLE_USER")
//                     .build());
//         } else {
//             return Mono.empty();
//         }
//     }
// }
