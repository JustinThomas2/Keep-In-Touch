package com.keepintouch.jobwatcher.model;

import com.keepintouch.domain.JobSourceType;
import java.util.Objects;
import java.util.UUID;

public record WatchedSourceConfig(
    UUID sourceId,
    UUID companyId,
    UUID userId,
    String companyName,
    JobSourceType sourceType,
    String originalSourceUrl,
    String canonicalSourceUrl) {

  public WatchedSourceConfig(
      UUID sourceId,
      UUID companyId,
      String companyName,
      JobSourceType sourceType,
      String originalSourceUrl,
      String canonicalSourceUrl) {
    this(sourceId, companyId, null, companyName, sourceType, originalSourceUrl, canonicalSourceUrl);
  }

  public WatchedSourceConfig {
    Objects.requireNonNull(sourceId, "sourceId must not be null");
    Objects.requireNonNull(companyId, "companyId must not be null");
    companyName = requireText(companyName, "companyName");
    Objects.requireNonNull(sourceType, "sourceType must not be null");
    originalSourceUrl = requireText(originalSourceUrl, "originalSourceUrl");
    canonicalSourceUrl = blankToNull(canonicalSourceUrl);
  }

  public String effectiveSourceUrl() {
    return canonicalSourceUrl == null ? originalSourceUrl : canonicalSourceUrl;
  }

  private static String requireText(String value, String fieldName) {
    String normalized = blankToNull(value);
    if (normalized == null) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return normalized;
  }

  private static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
