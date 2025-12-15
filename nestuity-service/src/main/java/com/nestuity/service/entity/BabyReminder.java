package com.nestuity.service.entity;

import com.nestuity.service.type.Frequency;
import com.nestuity.service.type.ReminderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "baby_reminder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BabyReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "baby_id", nullable = false, foreignKey = @ForeignKey(name = "fk_baby_task_baby"))
    private Baby baby;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Column
    private Integer occurrence;

    @Column(name = "requires_action")
    private Boolean requiresAction;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_on")
    private LocalDateTime completedOn; // tracks when the task was completed

    @Column(name = "user_created", nullable = false)
    private boolean userCreated = false;
}
