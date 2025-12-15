package com.nestuity.service.config;

import com.mailjet.client.MailjetClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailjetConfig {

    @Value("${spring.mailjet.api-key}")
    private String apiKey;

    @Value("${spring.mailjet.api-secret}")
    private String apiSecret;

    @Bean
    public MailjetClient mailjetClient() {
        return new MailjetClient(apiKey, apiSecret);
    }
}
