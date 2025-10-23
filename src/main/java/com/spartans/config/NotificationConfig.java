package com.spartans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationConfig {

  private String reminder;
  private String gscriptUrl;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public String getReminder() {
    return reminder;
  }

  public void setReminder(String reminder) {
    this.reminder = reminder;
  }

  public String getGscriptUrl() {
    return gscriptUrl;
  }

  public void setGscriptUrl(String gscriptUrl) {
    this.gscriptUrl = gscriptUrl;
  }
}
