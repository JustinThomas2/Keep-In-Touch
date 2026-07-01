package com.keepintouch.jobwatcher.model;

import com.keepintouch.domain.RemotePreference;
import java.util.List;
import java.util.Objects;

public record JobMatchCriteria(
    List<String> includeKeywords,
    List<String> excludeKeywords,
    List<String> includeCountries,
    List<String> includeLocations,
    RemotePreference remotePreference) {

  public JobMatchCriteria {
    includeKeywords = normalizeList(includeKeywords);
    excludeKeywords = normalizeList(excludeKeywords);
    includeCountries = normalizeList(includeCountries);
    includeLocations = normalizeList(includeLocations);
    remotePreference =
        Objects.requireNonNull(remotePreference, "remotePreference must not be null");
  }

  private static List<String> normalizeList(List<String> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();
  }
}
