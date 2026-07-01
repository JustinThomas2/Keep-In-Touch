package com.keepintouch.jobwatcher.logic;

import com.keepintouch.domain.RemotePreference;
import com.keepintouch.jobwatcher.model.JobMatchCriteria;
import com.keepintouch.jobwatcher.model.MatchResult;
import com.keepintouch.jobwatcher.model.NormalizedJob;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class DeterministicJobMatcher {

  private static final String UNCERTAIN_REMOTE_REASON =
      "Remote location is ambiguous; country could not be confirmed.";

  private DeterministicJobMatcher() {}

  public static MatchResult match(NormalizedJob job, JobMatchCriteria criteria) {
    String searchableText =
        normalize(
            String.join(
                " ",
                Stream.of(
                        job.title(),
                        job.location(),
                        job.country(),
                        job.department(),
                        job.jobCategory(),
                        job.experienceLevel(),
                        job.descriptionSnippet())
                    .filter(value -> value != null && !value.isBlank())
                    .toList()));
    String blockingText =
        normalize(
            String.join(
                " ",
                Stream.of(job.title(), job.department(), job.jobCategory(), job.experienceLevel())
                    .filter(value -> value != null && !value.isBlank())
                    .toList()));

    List<String> includeMatches = matchingTerms(searchableText, criteria.includeKeywords());
    List<String> excludeMatches = matchingTerms(blockingText, criteria.excludeKeywords());
    List<String> locationMatches = locationMatches(job, criteria);
    boolean ambiguousRemote = isAmbiguousRemoteAllowed(job, criteria);
    List<String> reasons = new ArrayList<>();

    if (!criteria.includeKeywords().isEmpty() && includeMatches.isEmpty()) {
      reasons.add("No include keywords matched.");
    }
    if (!excludeMatches.isEmpty()) {
      reasons.add("Excluded keywords matched: " + String.join(", ", excludeMatches) + ".");
    }
    if (ambiguousRemote) {
      reasons.add(UNCERTAIN_REMOTE_REASON);
    }
    if (!matchesLocation(job, criteria, locationMatches) && !ambiguousRemote) {
      reasons.add("Location did not match configured country, location, or remote preference.");
    }

    if (includeMatches.isEmpty() || !excludeMatches.isEmpty()) {
      return MatchResult.rejected(includeMatches, excludeMatches, locationMatches, reasons);
    }
    if (!matchesLocation(job, criteria, locationMatches) && !ambiguousRemote) {
      return MatchResult.rejected(includeMatches, excludeMatches, locationMatches, reasons);
    }
    return new MatchResult(true, includeMatches, List.of(), locationMatches, reasons);
  }

  private static boolean matchesLocation(
      NormalizedJob job, JobMatchCriteria criteria, List<String> locationMatches) {
    if (criteria.remotePreference() == RemotePreference.REMOTE_ALLOWED) {
      return true;
    }
    if (criteria.remotePreference() == RemotePreference.ONSITE_ONLY && isRemote(job.location())) {
      return false;
    }
    if (!locationMatches.isEmpty()) {
      return true;
    }
    if (criteria.includeCountries().isEmpty() && criteria.includeLocations().isEmpty()) {
      return true;
    }
    return false;
  }

  private static List<String> locationMatches(NormalizedJob job, JobMatchCriteria criteria) {
    List<String> matches = new ArrayList<>();
    String country = normalize(job.country());
    String location = normalize(job.location());
    CountryLocationClassifier.Classification classification =
        CountryLocationClassifier.classify(job.country(), job.location());

    for (String includeCountry : criteria.includeCountries()) {
      String normalizedCountry = normalizeCountry(includeCountry);
      if (!normalizedCountry.isEmpty()
          && countryMatches(normalizedCountry, country, location, classification)) {
        matches.add(includeCountry);
      }
    }

    for (String includeLocation : criteria.includeLocations()) {
      if (containsTerm(location, includeLocation)) {
        matches.add(includeLocation);
      }
    }

    if (criteria.remotePreference() == RemotePreference.REMOTE_US_ALLOWED
        && classification.remote()
        && classification.usBased()) {
      matches.add("remote-us");
    }
    return List.copyOf(matches);
  }

  private static boolean isAmbiguousRemoteAllowed(NormalizedJob job, JobMatchCriteria criteria) {
    if (criteria.remotePreference() != RemotePreference.REMOTE_US_ALLOWED) {
      return false;
    }
    CountryLocationClassifier.Classification classification =
        CountryLocationClassifier.classify(job.country(), job.location());
    String country = normalizeCountry(job.country());
    return classification.ambiguousRemote() && (country.isEmpty() || country.equals("unknown"));
  }

  private static boolean countryMatches(
      String expectedCountry,
      String country,
      String location,
      CountryLocationClassifier.Classification classification) {
    if (expectedCountry.equals("us") && classification.usBased()) {
      return true;
    }
    return country.equals(expectedCountry) || locationContainsCountry(location, expectedCountry);
  }

  private static boolean locationContainsCountry(String location, String country) {
    return switch (country) {
      case "us" ->
          location.contains("united states")
              || location.contains(" usa ")
              || containsTerm(location, "us");
      default -> containsTerm(location, country);
    };
  }

  private static boolean isRemote(String location) {
    return normalize(location).contains("remote");
  }

  private static List<String> matchingTerms(String searchableText, List<String> terms) {
    return terms.stream().filter(term -> containsTerm(searchableText, term)).toList();
  }

  private static boolean containsTerm(String searchableText, String term) {
    String normalizedTerm = normalize(term);
    return !normalizedTerm.isEmpty() && searchableText.contains(normalizedTerm);
  }

  private static String normalizeCountry(String value) {
    String normalized = normalize(value).trim();
    if (normalized.equals("usa")
        || normalized.equals("u.s.")
        || normalized.equals("u.s.a.")
        || normalized.equals("united states")
        || normalized.equals("united states of america")) {
      return "us";
    }
    return normalized;
  }

  private static String normalize(String value) {
    if (value == null) {
      return "";
    }
    String normalized =
        value
            .trim()
            .replaceAll("(?i)front[-\\s]+end", "frontend")
            .replaceAll("\\s+", " ")
            .toLowerCase(Locale.ROOT);
    return " " + normalized + " ";
  }
}
