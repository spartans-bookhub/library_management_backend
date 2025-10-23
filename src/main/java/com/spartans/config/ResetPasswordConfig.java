package com.spartans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "reset-password")
public class ResetPasswordConfig {
  private String link;
  private long expiry;

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public long getExpiry() {
    return expiry;
  }

  public void setExpiry(long expiry) {
    this.expiry = expiry;
  }
}
