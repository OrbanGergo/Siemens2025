package com.siemens.internship;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testSaveAndFindAllIds() {
        Item item = new Item(null, "Name", "Desc", "NEW", "email@test.com");
        item = itemRepository.save(item);

        List<Long> ids = itemRepository.findAllIds();

        assertEquals(1, ids.size());
        assertEquals(item.getId(), ids.get(0));
    }
}
