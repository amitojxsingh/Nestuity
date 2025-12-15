package com.nestuity.service.dto;

import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;

import java.util.List;

public class ParentDTO {
    // Included inside BabyResponse to show Parent Info without sending entire User Entity
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Inventory> inventoryList;

    public ParentDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.inventoryList = user.getInventory();
    }

    // Getters
    public Long getId() {return id;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getEmail() {return email;}
    public List<Inventory> getInventoryList() {return inventoryList;}


    // Setters
    public void setId(Long id) {this.id = id;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setEmail(String email) {this.email = email;}
    public void setInventoryList(List<Inventory> inventoryList) {this.inventoryList = inventoryList;}
}