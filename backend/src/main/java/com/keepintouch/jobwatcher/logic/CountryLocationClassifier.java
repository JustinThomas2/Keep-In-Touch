package com.keepintouch.jobwatcher.logic;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

final class CountryLocationClassifier {

  private static final Set<String> US_COUNTRY_STRINGS =
      Set.of("us", "usa", "united states", "united states of america");
  private static final Set<String> US_STATE_ABBREVIATIONS =
      Set.of(
          "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA",
          "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
          "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT",
          "VA", "WA", "WV", "WI", "WY", "DC");
  private static final Pattern CITY_STATE_PATTERN =
      Pattern.compile(".*,\\s*([A-Za-z]{2})(?:\\s|$).*");

  private CountryLocationClassifier() {}

  static Classification classify(String country, String location) {
    boolean countryIsUs = isUsCountry(country);
    boolean locationIsUs = containsUsCountry(location) || containsCityState(location);
    boolean remote = isRemote(location);
    boolean ambiguousRemote = remote && isOnlyRemote(location) && !countryIsUs;

    return new Classification(countryIsUs || locationIsUs, remote, ambiguousRemote);
  }

  private static boolean isUsCountry(String value) {
    return US_COUNTRY_STRINGS.contains(normalizeCountryText(value));
  }

  private static boolean containsUsCountry(String value) {
    String normalized = " " + normalizeCountryText(value) + " ";
    return US_COUNTRY_STRINGS.stream()
        .anyMatch(usValue -> normalized.contains(" " + usValue + " "));
  }

  private static boolean containsCityState(String value) {
    if (value == null) {
      return false;
    }
    var matcher = CITY_STATE_PATTERN.matcher(value.trim());
    return matcher.matches()
        && US_STATE_ABBREVIATIONS.contains(matcher.group(1).toUpperCase(Locale.ROOT));
  }

  private static boolean isRemote(String value) {
    return normalizeCountryText(value).contains("remote");
  }

  private static boolean isOnlyRemote(String value) {
    return normalizeCountryText(value).equals("remote");
  }

  private static String normalizeCountryText(String value) {
    if (value == null) {
      return "";
    }
    return value
        .trim()
        .replace(".", "")
        .replaceAll("[^A-Za-z0-9]+", " ")
        .replaceAll("\\s+", " ")
        .trim()
        .toLowerCase(Locale.ROOT);
  }

  record Classification(boolean usBased, boolean remote, boolean ambiguousRemote) {}
}
