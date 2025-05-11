package com.siemens.internship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing Item entities.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Fetches all item IDs.
     *
     * @return List of item IDs.
     */
    @Query("SELECT id FROM Item")
    List<Long> findAllIds();
}
