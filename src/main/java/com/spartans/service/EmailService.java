package com.spartans.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

  @Value("${google.script.url}")
  private String googleScriptUrl;

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
      restTemplate.postForEntity(googleScriptUrl, request, String.class);
      System.out.println("Email sent successfully via Google Script to " + toEmail);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Error sending email via Google Script: " + e.getMessage());
    }
  }
}
