package com.siemens.internship;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
    }

    @Test
    void testCreateAndGetItem() {
        Item item = new Item(null, "Test Item", "Test Description", "NEW", "test@example.com");

        // Create item
        ResponseEntity<Item> postResponse = restTemplate.postForEntity("/api/items", item, Item.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getBody()).isNotNull();
        assertThat(postResponse.getBody().getId()).isNotNull();

        Long id = postResponse.getBody().getId();

        // Get item
        ResponseEntity<Item> getResponse = restTemplate.getForEntity("/api/items/" + id, Item.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Test Item");
    }

    @Test
    void testGetAllItems() {
        itemRepository.save(new Item(null, "Item1", "Desc1", "NEW", "a@b.com"));
        itemRepository.save(new Item(null, "Item2", "Desc2", "NEW", "b@b.com"));

        ResponseEntity<Item[]> response = restTemplate.getForEntity("/api/items", Item[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testDeleteItem() {
        Item item = itemRepository.save(new Item(null, "ToDelete", "Desc", "NEW", "x@x.com"));

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/items/" + item.getId(), HttpMethod.DELETE, null, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(itemRepository.findById(item.getId())).isEmpty();
    }

    @Test
    void testUpdateItem() {
        Item item = itemRepository.save(new Item(null, "Original", "Original desc", "NEW", "orig@x.com"));
        item.setName("Updated");
        item.setDescription("Updated Desc");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Item> entity = new HttpEntity<>(item, headers);
        ResponseEntity<Item> response = restTemplate.exchange(
                "/api/items/" + item.getId(), HttpMethod.PUT, entity, Item.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Updated");
    }
}
