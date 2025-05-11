package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service class containing business logic for managing Items.
 */
@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * Retrieves all items from the repository.
     */
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     * Retrieves a specific item by ID.
     */
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    /**
     * Saves an item to the repository.
     */
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    /**
     * Deletes an item by ID.
     */
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Asynchronously processes all items by updating their status and returning the processed list.
     *
     * @return CompletableFuture containing a list of successfully processed items.
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        List<Long> itemIds = itemRepository.findAllIds();

        List<CompletableFuture<Optional<?>>> futures = itemIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Optional<Item> optItem = itemRepository.findById(id);
                        if (optItem.isPresent()) {
                            Item item = optItem.get();
                            item.setStatus("PROCESSED");
                            return Optional.of(itemRepository.save(item));
                        }
                        return Optional.empty();
                    } catch (Exception e) {
                        return Optional.empty(); // log and continue
                    }
                }, executor)).toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> (List<Item>) futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList());
    }
}
