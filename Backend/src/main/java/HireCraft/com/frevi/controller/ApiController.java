package com.frevi.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ApiController {

    // Імітація бази даних (тимчасове сховище)
    private Map<Long, String> items = new HashMap<>();
    private long nextId = 1;

    // === 1. Отримання даних (GET) ===

    @GetMapping("/hello")
    public String hello() {
        return "Повідомлення від бекенду!";
    }

    @GetMapping("/items")
    public Map<Long, String> getAllItems() {
        return items;
    }

    @GetMapping("/items/{id}")
    public String getItemById(@PathVariable Long id) {
        return items.getOrDefault(id, "Item not found");
    }

    // === 2. Створення даних (POST) ===

    @PostMapping("/items")
    public String addItem(@RequestBody String newItem) {
        long id = nextId++;
        items.put(id, newItem);
        return "Added item with ID: " + id;
    }

    @PostMapping("/items/bulk")
    public String addMultipleItems(@RequestBody List<String> newItems) {
        newItems.forEach(item -> {
            long id = nextId++;
            items.put(id, item);
        });
        return "Added " + newItems.size() + " items";
    }

    // === 3. Оновлення даних (PUT/PATCH) ===

    @PutMapping("/items/{id}")
    public String updateItem(@PathVariable Long id, @RequestBody String updatedItem) {
        if (items.containsKey(id)) {
            items.put(id, updatedItem);
            return "Item updated";
        }
        return "Item not found";
    }

    @PatchMapping("/items/{id}")
    public String partiallyUpdateItem(@PathVariable Long id, @RequestBody String partialUpdate) {
        items.computeIfPresent(id, (key, oldValue) -> oldValue + " | " + partialUpdate);
        return "Item patched";
    }

    // === 4. Видалення даних (DELETE) ===

    @DeleteMapping("/items/{id}")
    public String deleteItem(@PathVariable Long id) {
        items.remove(id);
        return "Item deleted";
    }

    @DeleteMapping("/items")
    public String deleteAllItems() {
        items.clear();
        nextId = 1;
        return "All items deleted";
    }

    // === 5. Спеціальні методи ===

    @GetMapping("/search")
    public Map<Long, String> searchItems(@RequestParam String keyword) {
        Map<Long, String> result = new HashMap<>();
        items.forEach((id, item) -> {
            if (item.contains(keyword)) {
                result.put(id, item);
            }
        });
        return result;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return Map.of(
                "status", "OK",
                "itemCount", items.size(),
                "timestamp", new Date()
        );
    }
}