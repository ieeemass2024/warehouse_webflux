package com.example.warehouse.dao;

import com.example.warehouse.bean.Item;
import com.example.warehouse.mapper.ItemRepository;
import com.example.warehouse.server.ItemService;

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
import static org.mockito.Mockito.when;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllItems() {
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Item 1");
        item1.setPrice(100.0);

        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Item 2");
        item2.setPrice(200.0);

        when(itemRepository.findAll()).thenReturn(Flux.just(item1, item2));

        Flux<Item> items = itemService.findAllItems();

        StepVerifier.create(items)
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();
    }

    @Test
    public void testGetItemById() {
        Item item = new Item();
        item.setId(1);
        item.setName("Item 1");
        item.setPrice(100.0);

        when(itemRepository.findById(anyInt())).thenReturn(Mono.just(item));

        Mono<Item> foundItem = itemService.getItemById(1);

        StepVerifier.create(foundItem)
                .expectNext(item)
                .verifyComplete();
    }

    @Test
    public void testSaveItem() {
        Item item = new Item();
        item.setId(1);
        item.setName("NewItem");
        item.setPrice(150.0);

        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));

        Mono<Item> savedItem = itemService.saveItem(item);

        StepVerifier.create(savedItem)
                .expectNext(item)
                .verifyComplete();
    }

    @Test
    public void testDeleteItemById() {
        when(itemRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        Mono<Void> deletedItem = itemService.deleteItemById(1);

        StepVerifier.create(deletedItem)
                .verifyComplete();
    }

    @Test
    public void testUpdateItem() {
        Item item = new Item();
        item.setId(1);
        item.setName("UpdatedItem");
        item.setPrice(200.0);

        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));

        Mono<Item> updatedItem = itemService.updateItem(item);

        StepVerifier.create(updatedItem)
                .expectNext(item)
                .verifyComplete();
    }
}
