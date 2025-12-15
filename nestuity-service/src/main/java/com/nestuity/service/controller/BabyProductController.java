package com.nestuity.service.controller;

import com.nestuity.service.dto.BabyProductResponse;
import com.nestuity.service.dto.CreateBabyProductRequest;
import com.nestuity.service.dto.UpdateBabyProductRequest;
import com.nestuity.service.service.BabyProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/baby-products")
public class BabyProductController {

    private final BabyProductService babyProductService;
    
    public BabyProductController(BabyProductService babyProductService) {
        this.babyProductService = babyProductService;
    }

    @PostMapping
    public ResponseEntity<BabyProductResponse> createBabyProduct(@RequestBody CreateBabyProductRequest request) {
        BabyProductResponse created = babyProductService.createBabyProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BabyProductResponse> updateBabyProduct(
            @PathVariable Long id, 
            @RequestBody UpdateBabyProductRequest request) {
        BabyProductResponse updated = babyProductService.updateBabyProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BabyProductResponse> getBabyProductById(@PathVariable Long id) {
        BabyProductResponse product = babyProductService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<BabyProductResponse>> getAllBabyProducts() {
        List<BabyProductResponse> products = babyProductService.findAll();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBabyProduct(@PathVariable Long id) {
        babyProductService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
