package com.nestuity.service.service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private MailjetClient mockClient;
    private EmailService emailService;

    @BeforeEach
    void setUp() throws Exception {
        mockClient = mock(MailjetClient.class);
        emailService = new EmailService(mockClient);

        // Set private fields via reflection
        setPrivateField(emailService, "FROM_EMAIL", "noreply@nestuity.com");
        setPrivateField(emailService, "TEMPLATE_WELCOME", 123L);
        setPrivateField(emailService, "TEMPLATE_DIAPER_REMINDER", 456L);
        setPrivateField(emailService, "TEMPLATE_WEEKLY_SUMMARY", 789L);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = EmailService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void sendWelcomeEmail_Success() throws MailjetException, IOException {
        MailjetResponse mockResponse = mock(MailjetResponse.class);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.getData()).thenReturn(new JSONArray());
        when(mockClient.post(any(MailjetRequest.class))).thenReturn(mockResponse);

        assertDoesNotThrow(() ->
                emailService.sendWelcomeEmail("test@example.com", "ParentUser"));

        // Only verify that post was called once
        verify(mockClient, times(1)).post(any(MailjetRequest.class));
    }

    @Test
    void sendWelcomeEmail_Failure_ThrowsIOException() throws MailjetException {
        when(mockClient.post(any(MailjetRequest.class)))
                .thenThrow(new MailjetException("Network down"));

        IOException ex = assertThrows(IOException.class, () ->
                emailService.sendWelcomeEmail("fail@example.com", "FailUser"));

        assertTrue(ex.getMessage().contains("Mailjet error"));
    }

    @Test
    void sendDiaperReminderEmail_Success() throws MailjetException, IOException {
        MailjetResponse mockResponse = mock(MailjetResponse.class);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.getData()).thenReturn(new JSONArray());
        when(mockClient.post(any(MailjetRequest.class))).thenReturn(mockResponse);

        assertDoesNotThrow(() -> emailService.sendDiaperReminderEmail("parent@example.com", "ParentUser", 3));

        ArgumentCaptor<MailjetRequest> captor = ArgumentCaptor.forClass(MailjetRequest.class);
        verify(mockClient, times(1)).post(captor.capture());
    }

    @Test
    void sendWeeklySummaryEmail_Success() throws MailjetException, IOException {
        MailjetResponse mockResponse = mock(MailjetResponse.class);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.getData()).thenReturn(new JSONArray());
        when(mockClient.post(any(MailjetRequest.class))).thenReturn(mockResponse);

        List<Map<String, String>> overdueTasks = List.of(Map.of("taskName", "Feed Baby", "daysOverdue", "2"));
        List<Map<String, String>> vaccinations = List.of(Map.of("vaccinationName", "MMR", "daysOverdue", "5"));

        assertDoesNotThrow(() ->
                emailService.sendWeeklySummaryEmail("weekly@example.com", "ParentUser", overdueTasks, vaccinations));

        ArgumentCaptor<MailjetRequest> captor = ArgumentCaptor.forClass(MailjetRequest.class);
        verify(mockClient, times(1)).post(captor.capture());
    }

    @Test
    void sendTemplateEmail_ErrorStatus_ThrowsIOException() throws MailjetException, IOException {
        MailjetResponse mockResponse = mock(MailjetResponse.class);
        when(mockResponse.getStatus()).thenReturn(400);
        when(mockResponse.getData()).thenReturn(new JSONArray("[{\"ErrorInfo\":\"Bad Request\"}]"));

        when(mockClient.post(any(MailjetRequest.class))).thenReturn(mockResponse);

        IOException exception = assertThrows(IOException.class, () ->
                emailService.sendWelcomeEmail("fail@example.com", "FailUser"));

        assertTrue(exception.getMessage().contains("Mailjet error"));
    }

    @Test
    void sendTemplateEmail_ExceptionDuringSend_ThrowsIOException() throws MailjetException {
        when(mockClient.post(any(MailjetRequest.class))).thenThrow(new MailjetException("Network down"));

        IOException exception = assertThrows(IOException.class, () ->
                emailService.sendWelcomeEmail("offline@example.com", "OfflineUser"));

        assertTrue(exception.getMessage().contains("Mailjet error"));
    }
}
