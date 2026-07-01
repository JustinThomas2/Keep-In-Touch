package com.keepintouch.jobwatcher.model;

import java.time.OffsetDateTime;

public record NormalizedJob(
    String externalId,
    String title,
    String location,
    String country,
    String url,
    String canonicalUrl,
    String applyUrl,
    String department,
    String jobCategory,
    String experienceLevel,
    OffsetDateTime postedAt,
    String descriptionSnippet) {

  public NormalizedJob {
    externalId = blankToNull(externalId);
    title = requireText(title, "title");
    location = blankToNull(location);
    country = normalizeCountry(country);
    url = requireText(url, "url");
    canonicalUrl = blankToNull(canonicalUrl);
    applyUrl = blankToNull(applyUrl);
    department = blankToNull(department);
    jobCategory = blankToNull(jobCategory);
    experienceLevel = blankToNull(experienceLevel);
    descriptionSnippet = blankToNull(descriptionSnippet);
  }

  public String effectiveUrl() {
    return canonicalUrl == null ? url : canonicalUrl;
  }

  private static String requireText(String value, String fieldName) {
    String normalized = blankToNull(value);
    if (normalized == null) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return normalized;
  }

  private static String normalizeCountry(String value) {
    String normalized = blankToNull(value);
    if (normalized == null) {
      return null;
    }
    if (normalized.equalsIgnoreCase("usa") || normalized.equalsIgnoreCase("united states")) {
      return "US";
    }
    return normalized.toUpperCase();
  }

  private static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
