package com.example.warehouse.handler;

import com.example.warehouse.bean.Item;
import com.example.warehouse.server.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ItemHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ItemHandler.class);
    private static final String ALL_ITEMS_CACHE_KEY = "allItems";

    @Autowired
    private final ItemService itemService;
    @Autowired
    private final ReactiveRedisOperations<String, Object> itemOps;

    public ItemHandler(ItemService itemService, ReactiveRedisOperations<String, Object> itemOps) {
        this.itemService = itemService;
        this.itemOps = itemOps;
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public Mono<ServerResponse> getAllItems(ServerRequest request) {
        String operationType = "getAllItems";
        return getUserDetails()
                .flatMap(userDetails -> {
                    LOG.info("User {} is performing {} operation", userDetails.getUsername(), operationType);
                    return itemOps.opsForValue().get(ALL_ITEMS_CACHE_KEY)
                            .switchIfEmpty(
                                    itemService.findAllItems()
                                            .collectList()
                                            .flatMap(items -> itemOps.opsForValue().set(ALL_ITEMS_CACHE_KEY, items).thenReturn(items))
                            )
                            .flatMap(items -> ok().bodyValue(items))
                            .doOnSuccess(response -> logOperation(userDetails.getUsername(), operationType, "SUCCESS"))
                            .doOnError(error -> logOperation(userDetails.getUsername(), operationType, "ERROR: " + error.getMessage()));
                });
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public Mono<ServerResponse> getItemById(ServerRequest request) {
        String operationType = "getItemById";
        String id = request.pathVariable("id");
        String cacheKey = "item:" + id;
        return getUserDetails()
                .flatMap(userDetails -> {
                    LOG.info("User {} is performing {} operation for item ID {}", userDetails.getUsername(), operationType, id);
                    return itemOps.opsForValue().get(cacheKey)
                            .switchIfEmpty(
                                    itemService.getItemById(Integer.valueOf(id))
                                            .flatMap(item -> itemOps.opsForValue().set(cacheKey, item).thenReturn(item))
                            )
                            .flatMap(item -> ok().bodyValue(item))
                            .switchIfEmpty(ServerResponse.notFound().build())
                            .doOnSuccess(response -> logOperation(userDetails.getUsername(), operationType, "SUCCESS"))
                            .doOnError(error -> logOperation(userDetails.getUsername(), operationType, "ERROR: " + error.getMessage()));
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<ServerResponse> createItem(ServerRequest request) {
        String operationType = "createItem";
        return getUserDetails()
                .flatMap(userDetails -> {
                    LOG.info("User {} is performing {} operation", userDetails.getUsername(), operationType);
                    Mono<Item> itemMono = request.bodyToMono(Item.class)
                            .doOnNext(item -> LOG.info("Received item data: {}", item))
                            .doOnError(error -> LOG.error("Error parsing item data", error));

                    return itemMono.flatMap(itemService::saveItem)
                            .flatMap(savedItem -> {
                                String cacheKey = "item:" + savedItem.getId();
                                // 清除与项目相关的缓存键
                                return itemOps.opsForValue().delete(ALL_ITEMS_CACHE_KEY)
                                        .then(itemOps.opsForValue().set(cacheKey, savedItem))
                                        .thenReturn(savedItem);
                            })
                            .flatMap(savedItem -> ServerResponse.status(201).bodyValue(savedItem))
                            .doOnSuccess(response -> logOperation(userDetails.getUsername(), operationType, "SUCCESS"))
                            .doOnError(error -> logOperation(userDetails.getUsername(), operationType, "ERROR: " + error.getMessage()));
                });
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<ServerResponse> updateItem(ServerRequest request) {
        String operationType = "updateItem";
        String id = request.pathVariable("id");
        String cacheKey = "item:" + id;
        return getUserDetails()
                .flatMap(userDetails -> {
                    LOG.info("User {} is performing {} operation for item ID {}", userDetails.getUsername(), operationType, id);
                    Mono<Item> itemMono = request.bodyToMono(Item.class).doOnNext(item -> item.setId(Integer.valueOf(id)));
                    return itemMono.flatMap(itemService::updateItem)
                            .flatMap(updatedItem -> {
                                // 清除缓存
                                return itemOps.opsForValue().delete(ALL_ITEMS_CACHE_KEY)
                                        .then(itemOps.opsForValue().delete(cacheKey))
                                        .then(itemOps.opsForValue().set(cacheKey, updatedItem))
                                        .thenReturn(updatedItem);
                            })
                            .flatMap(item -> ok().bodyValue(item))
                            .doOnSuccess(response -> logOperation(userDetails.getUsername(), operationType, "SUCCESS"))
                            .doOnError(error -> logOperation(userDetails.getUsername(), operationType, "ERROR: " + error.getMessage()));
                });
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<ServerResponse> deleteItem(ServerRequest request) {
        String operationType = "deleteItem";
        String id = request.pathVariable("id");
        String cacheKey = "item:" + id;
        return getUserDetails()
                .flatMap(userDetails -> {
                    LOG.info("User {} is performing {} operation for item ID {}", userDetails.getUsername(), operationType, id);
                    return itemService.deleteItemById(Integer.valueOf(id))
                            .then(itemOps.opsForValue().delete(cacheKey))
                            .then(itemOps.opsForValue().delete(ALL_ITEMS_CACHE_KEY))
                            .then(ok().build())
                            .doOnSuccess(response -> logOperation(userDetails.getUsername(), operationType, "SUCCESS"))
                            .doOnError(error -> logOperation(userDetails.getUsername(), operationType, "ERROR: " + error.getMessage()));
                });
    }

    private Mono<UserDetails> getUserDetails() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> (UserDetails) context.getAuthentication().getPrincipal());
    }

    private void logOperation(String username, String operationType, String result) {
        LOG.info("User {} performed {} with result: {}", username, operationType, result);
    }
}
