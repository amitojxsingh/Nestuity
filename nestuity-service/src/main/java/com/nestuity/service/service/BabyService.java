package com.nestuity.service.service;

import com.nestuity.service.entity.Baby;
import com.nestuity.service.repository.BabyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BabyService {

    private final BabyRepository babyRepository;

    public BabyService(BabyRepository babyRepository) {
        this.babyRepository = babyRepository;
    }

    public Baby createBaby(Baby baby) {
        return babyRepository.save(baby);
    }

    public List<Baby> getAllBabies() {
        return babyRepository.findAll();
    }

    public Optional<Baby> getBabyById(Long id) {
        return babyRepository.findById(id);
    }

    public List<Baby> getBabiesByUserId(Long userId) {
        return babyRepository.findByUserId(userId);
    }

    public Baby updateBaby(Baby baby) {
        return babyRepository.save(baby);
    }

    public void deleteBaby(Long id) {
        babyRepository.deleteById(id);
    }
}
