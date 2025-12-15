package com.nestuity.service.service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final MailjetClient client;

    @Value("${spring.mailjet.from_email}")
    private String FROM_EMAIL;

    // Template IDs from Mailjet
    @Value("${spring.mailjet.template.welcome_id}")
    private Long TEMPLATE_WELCOME;

    @Value("${spring.mailjet.template.diaper_reminder_id}")
    private Long TEMPLATE_DIAPER_REMINDER;

    @Value("${spring.mailjet.template.weekly_summary_id}")
    private Long TEMPLATE_WEEKLY_SUMMARY;

    public EmailService(MailjetClient client) {
        this.client = client;
    }

    // ========== WELCOME EMAIL ===========================================
    public void sendWelcomeEmail(String to, String username) throws IOException {
        JSONObject variables = new JSONObject().put("username", username);

        System.out.println("[WELCOME EMAIL] To: " + to);
        System.out.println("[WELCOME EMAIL] Template ID: " + TEMPLATE_WELCOME);
        System.out.println("[WELCOME EMAIL] Variables: " + variables.toString(2));

        sendTemplateEmail(to, TEMPLATE_WELCOME, variables);
    }

    // ========== DIAPER REMINDER EMAIL ==================================
    public void sendDiaperReminderEmail(String to, String username, Integer daysLeft) throws IOException {
        JSONObject variables = new JSONObject()
                .put("username", username)
                .put("daysLeft", daysLeft);

        sendTemplateEmail(to, TEMPLATE_DIAPER_REMINDER, variables);
    }

    // ========== WEEKLY SUMMARY EMAIL ===================================
    public void sendWeeklySummaryEmail(String to, String username,
                                       List<Map<String, String>> overdueTasks,
                                       List<Map<String, String>> vaccinationsOverdue) throws IOException {

        boolean hasAnyOverdue = (overdueTasks != null && !overdueTasks.isEmpty())
                || (vaccinationsOverdue != null && !vaccinationsOverdue.isEmpty());

        JSONObject variables = new JSONObject()
                .put("username", username)
                .put("hasAnyOverdue", hasAnyOverdue)
                .put("overdueTasks", overdueTasks)
                .put("vaccinationsOverdue", vaccinationsOverdue);


        sendTemplateEmail(to, TEMPLATE_WEEKLY_SUMMARY, variables);
    }

    // =====================================================================
    // UNIVERSAL TEMPLATE SENDER (Mailjet)
    // =====================================================================
    private void sendTemplateEmail(String to, Long templateId, JSONObject variables) throws IOException {

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", FROM_EMAIL)
                                        .put("Name", "Nestuity"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject().put("Email", to)))
                                .put(Emailv31.Message.TEMPLATEID, templateId)
                                .put(Emailv31.Message.TEMPLATELANGUAGE, true)
                                .put(Emailv31.Message.VARIABLES, variables)
                        )
                );

        try {
            MailjetResponse response = client.post(request);

            if (response.getStatus() >= 400) {
                throw new IOException("Mailjet error: " + response.getData());
            }

        } catch (Exception ex) {
            System.out.println("[MAILJET SEND ERROR] " + ex.getMessage());
            throw new IOException("Mailjet error: " + ex.getMessage(), ex);
        }
    }
}
