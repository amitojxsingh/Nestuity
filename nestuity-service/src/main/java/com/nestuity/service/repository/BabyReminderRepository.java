package com.nestuity.service.repository;

import com.nestuity.service.entity.BabyReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BabyReminderRepository extends JpaRepository<BabyReminder, Long> {

    List<BabyReminder> findByBabyId(Long babyId);

}