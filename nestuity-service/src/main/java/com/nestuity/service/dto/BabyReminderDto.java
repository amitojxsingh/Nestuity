package com.nestuity.service.dto;

import com.nestuity.service.type.Frequency;
import com.nestuity.service.type.ReminderRange;
import com.nestuity.service.type.ReminderType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BabyReminderDto {
    public Long id;
    public Long babyId;
    public ReminderType type;
    public String title;
    public String description;
    public Frequency frequency;
    public Integer occurrence; // free-form as in entity
    public Boolean requiresAction;
    public String notes;
    public LocalDateTime completedOn;
    public LocalDateTime nextDue; // computed by the service
    private ReminderRange range;
    public LocalDateTime startDate;
    public boolean userCreated;
}