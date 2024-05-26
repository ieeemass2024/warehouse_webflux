package com.example.warehouse.dao;

import com.example.warehouse.bean.User;
import com.example.warehouse.mapper.UserRepository;
import com.example.warehouse.server.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUserList() {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");

        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));

        StepVerifier.create(userService.userList())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
    }

    @Test
    public void testSave() {
        User user = new User();
        user.setId(1);
        user.setUsername("user1");
        user.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userService.save(user))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setId(1);
        user.setUsername("user1");
        user.setPassword("newpassword");

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userService.update(user))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setId(1);
        user.setUsername("user1");

        when(userRepository.findById(anyInt())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.findUserById(1))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void testDelete() {
        when(userRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(userService.delete(1))
                .verifyComplete();
    }

    @Test
    public void testAuthenticate() {
        User user = new User();
        user.setId(1);
        user.setUsername("user1");
        user.setPassword("password");

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.authenticate("user1", "password"))
                .expectNext(user)
                .verifyComplete();

        // Test incorrect password
        StepVerifier.create(userService.authenticate("user1", "wrongpassword"))
                .expectComplete()
                .verify();
    }
}
