package com.nestuity.service.repository;

import com.nestuity.service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Handles all DB operations for Inventory

    // Find all the items in the User's Inventory
    List<Inventory> findByUserId(Long id);

    // Find a User's specific item (ex. diapers, wipes, etc) by supply name & user ID
    Optional<Inventory> findByUserIdAndSupplyNameIgnoreCase(Long userId, String supplyName);


}
