package com.example.warehouse.mapper;

import com.example.warehouse.bean.Item;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ItemRepository extends ReactiveCrudRepository<Item, Integer> {
}
