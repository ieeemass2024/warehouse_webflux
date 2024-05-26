package com.example.warehouse.server;

import com.example.warehouse.bean.Item;
import com.example.warehouse.mapper.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemService {
    @Autowired
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Flux<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public Mono<Item> getItemById(Integer id) {
        return itemRepository.findById(id);
    }

    public Mono<Item> saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Mono<Void> deleteItemById(Integer id) {
        return itemRepository.deleteById(id);
    }

    public Mono<Item> updateItem(Item item) {
        return itemRepository.save(item);
    }
}
