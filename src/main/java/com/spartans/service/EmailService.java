package com.spartans.service;

import com.spartans.config.NotificationConfig;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

  @Autowired private NotificationConfig notificationConfig;

  @Autowired private RestTemplate restTemplate;

  public void sendEmail(String toEmail, String subject, String body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, String> payload = new HashMap<>();
    payload.put("to", toEmail);
    payload.put("subject", subject);
    payload.put("body", body);
    HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

    try {
      ResponseEntity<String> response =
          restTemplate.postForEntity(notificationConfig.getGscriptUrl(), request, String.class);
      System.out.println("Response status code from MailApp: " + response.getStatusCode());
      System.out.println("Email sent successfully via Google Script to " + toEmail);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Error sending email via Google Script: " + e.getMessage());
    }
  }
}
