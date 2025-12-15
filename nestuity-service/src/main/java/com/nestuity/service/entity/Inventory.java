package com.nestuity.service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many inventory items belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private String supplyName;

    // Individual units - # of individual diapers
    @Column(nullable = false)
    private Double totalSingleQuantity;

    // Unit Quantity - # of boxes
    @Column(nullable = false)
    private Double totalUnitQuantity;

    // How many single units in one box, package
    @Column(nullable = false)
    private Double unitConversion;

    // Preferred Supply minimum
    @Column(nullable = false)
    private Integer preferredSupplyMin = 14;
}
