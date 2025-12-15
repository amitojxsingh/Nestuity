package com.nestuity.service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "baby")
public class Baby {
    // Attributes
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Each baby belongs to a user, a User can have multiple babies
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "date_of_birth")
    private Date dob;

    @Column(nullable = false)
    private String name;
    private Double weight;

    @Column(name = "diaper_size")
    private String diaperSize;

    private Integer dailyUsage = 0;

    // Getters
    public User getUser() {return user;}
    public Long getId() { return id; }
    public String getName() { return name; }
    public Date getDob() { return dob; }
    public Double getWeight() { return weight; }
    public String getDiaperSize() { return diaperSize; }
    public Integer getDailyUsage() { return dailyUsage; }

    // Setters
    public void setUser(User user) { this.user = user; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDob(Date dob) { this.dob = dob; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setDiaperSize(String diaperSize) { this.diaperSize = diaperSize; }
    public void setDailyUsage(Integer dailyUsage) { this.dailyUsage = dailyUsage; }
}
