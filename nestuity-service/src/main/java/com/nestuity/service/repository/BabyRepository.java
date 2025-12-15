package com.nestuity.service.repository;

import com.nestuity.service.entity.Baby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BabyRepository extends JpaRepository<Baby, Long> {
    // TODO: If need be you can add custom queries here if needed, e.g. findByName, findByDob, etc.
    List<Baby> findByUserId(Long userId);
}
