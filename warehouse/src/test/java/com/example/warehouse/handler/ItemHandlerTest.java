package com.example.warehouse.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.warehouse.bean.Item;
import com.example.warehouse.bean.LoginRequest;
import com.example.warehouse.bean.User;
import com.example.warehouse.config.SecurityConfig;
import com.example.warehouse.server.ItemService;
import com.example.warehouse.server.UserService;
import com.example.warehouse.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebFluxTest(ItemHandler.class)
@Import({ SecurityConfig.class, JwtUtils.class, UserHandler.class })
public class ItemHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @MockBean
    private ReactiveRedisOperations<String, Object> redisOperations;

    @MockBean
    private JwtUtils jwtUtils;

    private ReactiveValueOperations<String, Object> valueOperations;

    private String jwtToken;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化 ReactiveValueOperations mock
        valueOperations = mock(ReactiveValueOperations.class);
        when(redisOperations.opsForValue()).thenReturn(valueOperations);

        // 配置 jwtUtils 和 userService
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn("user1");
        when(jwtUtils.resolveJWT(anyString())).thenReturn(Mono.just(decodedJWT));

        User user = new User();
        user.setId(1);
        user.setUsername("user1");
        user.setPassword("pass1234");
        user.setRole("USER");

        when(userService.authenticate(anyString(), anyString())).thenReturn(Mono.just(user));
        when(jwtUtils.createJwt(any(), anyString(), anyString())).thenReturn("fake-jwt-token");

        // 设置模拟登录返回值
        LoginRequest loginRequest = new LoginRequest("user1", "pass1234");
        HashMap<String, Object> loginResponse = new HashMap<>();
        loginResponse.put("jwtToken", "fake-jwt-token");
        loginResponse.put("user", user);

        // 模拟登录过程获取JWT
        when(userService.authenticate(anyString(), anyString())).thenReturn(Mono.just(user));
        when(jwtUtils.createJwt(any(), anyString(), anyString())).thenReturn("fake-jwt-token");

        jwtToken = "Bearer fake-jwt-token";
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    public void testGetAllItems() {
        System.out.println("Testing getAllItems with JWT Token: " + jwtToken);

        // Mock ItemService 和 Redis 操作
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setPrice(100.0);

        when(valueOperations.get("allItems")).thenReturn(Mono.empty());
        when(itemService.findAllItems()).thenReturn(Flux.just(item));
        when(valueOperations.set("allItems", Collections.singletonList(item))).thenReturn(Mono.empty());

        webTestClient.get().uri("/items")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isUnauthorized();
    
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    public void testGetItemById() {
        System.out.println("Testing getItemById with JWT Token: " + jwtToken);

        // Mock ItemService 和 Redis 操作
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setPrice(100.0);

        when(valueOperations.get("item:1")).thenReturn(Mono.empty());
        when(itemService.getItemById(anyInt())).thenReturn(Mono.just(item));
        when(valueOperations.set("item:1", item)).thenReturn(Mono.empty());

        webTestClient.get().uri("/items/1")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "user1", roles = { "ADMIN" })
    public void testCreateItem() {
        System.out.println("Testing createItem with JWT Token: " + jwtToken);

        // Mock ItemService 和 Redis 操作
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setPrice(100.0);

        when(itemService.saveItem(any(Item.class))).thenReturn(Mono.just(item));
        when(valueOperations.delete("allItems")).thenReturn(Mono.empty());

        webTestClient.post().uri("/items")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "user1", roles = { "ADMIN" })
    public void testUpdateItem() {
        System.out.println("Testing updateItem with JWT Token: " + jwtToken);

        // Mock ItemService 和 Redis 操作
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setPrice(100.0);

        when(itemService.updateItem(any(Item.class))).thenReturn(Mono.just(item));
        when(valueOperations.delete("allItems")).thenReturn(Mono.empty());
        when(valueOperations.set("item:1", item)).thenReturn(Mono.empty());

        webTestClient.put().uri("/items/1")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "user1", roles = { "ADMIN" })
    public void testDeleteItem() {
        System.out.println("Testing deleteItem with JWT Token: " + jwtToken);

        // Mock ItemService 和 Redis 操作
        when(itemService.deleteItemById(anyInt())).thenReturn(Mono.empty());
        when(valueOperations.delete("item:1")).thenReturn(Mono.empty());
        when(valueOperations.delete("allItems")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/items/1")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    
}
