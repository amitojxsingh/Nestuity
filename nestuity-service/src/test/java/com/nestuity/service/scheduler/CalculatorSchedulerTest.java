package com.nestuity.service.scheduler;

import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.entity.UserPreferences;
import com.nestuity.service.service.BabyService;
import com.nestuity.service.service.DiaperUsageCalculatorService;
import com.nestuity.service.service.EmailService;
import com.nestuity.service.service.InventoryService;
import com.nestuity.service.dto.DiaperUsageResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculatorSchedulerInventoryTest {

    @Mock
    private BabyService babyService;

    @Mock
    private EmailService emailService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private DiaperUsageCalculatorService diaperUsageCalculatorService;

    @InjectMocks
    private CalculatorScheduler calculatorScheduler;

    private User parent;
    private Baby baby;
    private Inventory diaperInventory;

    @BeforeEach
    void setUp() {
        parent = new User();
        parent.setId(1L);
        parent.setFirstName("Jane");
        parent.setLastName("Doe");
        parent.setEmail("jane@example.com");

        // Set up user preferences with email notifications enabled
        UserPreferences preferences = new UserPreferences();
        preferences.setEmailNotificationsEnabled(true);
        parent.setPreferences(preferences);

        baby = new Baby();
        baby.setId(1L);
        baby.setUser(parent);
        baby.setDailyUsage(10);

        diaperInventory = new Inventory();
        diaperInventory.setId(1L);
        diaperInventory.setSupplyName("diapers");
        diaperInventory.setTotalSingleQuantity(50.0);
        diaperInventory.setTotalUnitQuantity(5.0);
        diaperInventory.setUnitConversion(10.0); // 10 singles per box
        diaperInventory.setUser(parent);
    }

    @Test
    void testRunTask_UpdatesInventoryAndSendsEmail() throws Exception {
        double originalSingles = diaperInventory.getTotalSingleQuantity();
        double unitConversion = diaperInventory.getUnitConversion();

        when(babyService.getAllBabies()).thenReturn(Collections.singletonList(baby));
        when(inventoryService.getInventoryItemByUserAndSupplyName(parent.getId(), "diapers"))
                .thenReturn(diaperInventory);
        when(diaperUsageCalculatorService.calculateUsage(baby.getId()))
                .thenReturn(DiaperUsageResponse.builder().daysLeft(5).build());
        when(inventoryService.updateInventory(anyLong(), any(Inventory.class))).thenReturn(diaperInventory);

        calculatorScheduler.runTask();

        double expectedSingles = originalSingles - baby.getDailyUsage();
        double expectedUnits = expectedSingles / unitConversion;

        verify(inventoryService).updateInventory(eq(diaperInventory.getId()), argThat(inv ->
                inv.getTotalSingleQuantity() == expectedSingles &&
                        inv.getTotalUnitQuantity() == expectedUnits
        ));

        verify(emailService).sendDiaperReminderEmail(
                parent.getEmail(),
                parent.getFirstName() + " " + parent.getLastName(),
                5
        );
    }

    @Test
    void testRunTask_PreventsNegativeInventory() throws Exception {
        baby.setDailyUsage(60); // larger than current singles
        double originalSingles = diaperInventory.getTotalSingleQuantity();
        double unitConversion = diaperInventory.getUnitConversion();

        when(babyService.getAllBabies()).thenReturn(Collections.singletonList(baby));
        when(inventoryService.getInventoryItemByUserAndSupplyName(parent.getId(), "diapers"))
                .thenReturn(diaperInventory);
        when(diaperUsageCalculatorService.calculateUsage(baby.getId()))
                .thenReturn(DiaperUsageResponse.builder().daysLeft(2).build());
        when(inventoryService.updateInventory(anyLong(), any(Inventory.class))).thenReturn(diaperInventory);

        calculatorScheduler.runTask();

        verify(inventoryService).updateInventory(eq(diaperInventory.getId()), argThat(inv ->
                inv.getTotalSingleQuantity() == Math.max(0, originalSingles - baby.getDailyUsage()) &&
                        inv.getTotalUnitQuantity() == Math.max(0, (originalSingles - baby.getDailyUsage()) / unitConversion)
        ));

        verify(emailService).sendDiaperReminderEmail(
                parent.getEmail(),
                parent.getFirstName() + " " + parent.getLastName(),
                2
        );
    }

    @Test
    void testRunTask_HandlesMultipleBabies() throws Exception {
        User parent2 = new User();
        parent2.setId(2L);
        parent2.setFirstName("John");
        parent2.setLastName("Smith");
        parent2.setEmail("john@example.com");

        Baby baby2 = new Baby();
        baby2.setUser(parent2);
        baby2.setDailyUsage(5);

        Inventory inventory2 = new Inventory();
        inventory2.setId(2L);
        inventory2.setSupplyName("diapers");
        inventory2.setTotalSingleQuantity(20.0);
        inventory2.setUnitConversion(10.0);
        inventory2.setUser(parent2);

        UserPreferences prefs1 = new UserPreferences();
        prefs1.setEmailNotificationsEnabled(true);
        parent.setPreferences(prefs1);

        UserPreferences prefs2 = new UserPreferences();
        prefs2.setEmailNotificationsEnabled(true);
        parent2.setPreferences(prefs2);

        List<Baby> babies = Arrays.asList(baby, baby2);

        double originalSingles1 = diaperInventory.getTotalSingleQuantity();
        double unitConversion1 = diaperInventory.getUnitConversion();
        double originalSingles2 = inventory2.getTotalSingleQuantity();
        double unitConversion2 = inventory2.getUnitConversion();

        when(babyService.getAllBabies()).thenReturn(babies);
        when(inventoryService.getInventoryItemByUserAndSupplyName(parent.getId(), "diapers"))
                .thenReturn(diaperInventory);
        when(inventoryService.getInventoryItemByUserAndSupplyName(parent2.getId(), "diapers"))
                .thenReturn(inventory2);
        when(diaperUsageCalculatorService.calculateUsage(baby.getId()))
                .thenReturn(DiaperUsageResponse.builder().daysLeft(3).build());
        when(diaperUsageCalculatorService.calculateUsage(baby2.getId()))
                .thenReturn(DiaperUsageResponse.builder().daysLeft(5).build());
        when(inventoryService.updateInventory(anyLong(), any(Inventory.class)))
                .thenReturn(diaperInventory)
                .thenReturn(inventory2);

        calculatorScheduler.runTask();

        verify(inventoryService).updateInventory(eq(diaperInventory.getId()), argThat(inv ->
                inv.getTotalSingleQuantity() == originalSingles1 - baby.getDailyUsage() &&
                        inv.getTotalUnitQuantity() == (originalSingles1 - baby.getDailyUsage()) / unitConversion1
        ));

        verify(inventoryService).updateInventory(eq(inventory2.getId()), argThat(inv ->
                inv.getTotalSingleQuantity() == originalSingles2 - baby2.getDailyUsage() &&
                        inv.getTotalUnitQuantity() == (originalSingles2 - baby2.getDailyUsage()) / unitConversion2
        ));

        verify(emailService).sendDiaperReminderEmail(parent.getEmail(),
                parent.getFirstName() + " " + parent.getLastName(),
                3);

        verify(emailService).sendDiaperReminderEmail(parent2.getEmail(),
                parent2.getFirstName() + " " + parent2.getLastName(),
                5
        );
    }

    @Test
    void testRunTask_HandlesEmptyBabyList() throws Exception {
        when(babyService.getAllBabies()).thenReturn(Collections.emptyList());

        calculatorScheduler.runTask();

        verify(inventoryService, never()).updateInventory(anyLong(), any());
        verify(emailService, never()).sendDiaperReminderEmail(anyString(), anyString(), anyInt());
    }
}
