package com.spartans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "admin.report")
public class AdminReportConfig {

  private double fineThreshold;
  private long lateThreshold;

  public double getFineThreshold() {
    return fineThreshold;
  }

  public void setFineThreshold(double fineThreshold) {
    this.fineThreshold = fineThreshold;
  }

  public long getLateThreshold() {
    return lateThreshold;
  }

  public void setLateThreshold(long lateThreshold) {
    this.lateThreshold = lateThreshold;
  }
}