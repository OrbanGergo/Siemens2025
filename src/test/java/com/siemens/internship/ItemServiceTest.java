package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Item> mockItems = List.of(new Item(1L, "Test", "desc", "NEW", "a@test.com"));
        when(itemRepository.findAll()).thenReturn(mockItems);

        List<Item> result = itemService.findAll();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName());
    }

    @Test
    void testSave() {
        Item item = new Item(null, "Test", "desc", "NEW", "a@test.com");
        when(itemRepository.save(item)).thenReturn(item);

        Item saved = itemService.save(item);

        assertEquals("Test", saved.getName());
    }

    @Test
    void testFindById() {
        Item item = new Item(1L, "Test", "desc", "NEW", "a@test.com");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<Item> found = itemService.findById(1L);
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());
    }

    @Test
    void testProcessItemsAsync() throws Exception {
        List<Long> ids = List.of(1L);
        Item item = new Item(1L, "Name", "Desc", "OLD", "email@test.com");
        Item updated = new Item(1L, "Name", "Desc", "PROCESSED", "email@test.com");

        when(itemRepository.findAllIds()).thenReturn(ids);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updated);

        CompletableFuture<List<Item>> future = itemService.processItemsAsync();
        List<Item> result = future.get();

        assertEquals(1, result.size());
        assertEquals("PROCESSED", result.get(0).getStatus());
    }
}
