package com.nestuity.service.dto;

import com.nestuity.service.entity.Baby;

public class BabyResponse {
    // What the Client sees when fetching Babies from DB
    private Long id;
    private String name;
    private Double weight;
    private String diaperSize;
    private String dob;
    private Integer dailyUsage;
    private ParentDTO parent;

    public BabyResponse(Baby baby) {
        this.id = baby.getId();
        this.name = baby.getName();
        this.weight = baby.getWeight();
        this.diaperSize = baby.getDiaperSize();
        this.dob = baby.getDob() != null ? baby.getDob().toString() : null;
        this.dailyUsage = baby.getDailyUsage();
        this.parent = new ParentDTO(baby.getUser());
    }

    // Getters
    public Long getId() {return id;}
    public String getName() {return name;}
    public Double getWeight() {return weight;}
    public String getDiaperSize() {return diaperSize;}
    public String getDob() {return dob;}
    public Integer getDailyUsage() {return dailyUsage;}
    public ParentDTO getParent() {return parent;}

    // Setters
    public void setId(Long id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setWeight(Double weight) {this.weight = weight;}
    public void setDiaperSize(String diaperSize) {this.diaperSize = diaperSize;}
    public void setDob(String dob) {this.dob = dob;}
    public void setDailyUsage(Integer dailyUsage) { this.dailyUsage = dailyUsage; }
    public void setParent(ParentDTO parent) {this.parent = parent;}
}
