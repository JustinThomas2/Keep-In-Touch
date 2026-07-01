package com.keepintouch.jobwatcher.logic;

import static org.assertj.core.api.Assertions.assertThat;

import com.keepintouch.domain.RemotePreference;
import com.keepintouch.jobwatcher.model.JobMatchCriteria;
import com.keepintouch.jobwatcher.model.MatchResult;
import com.keepintouch.jobwatcher.model.NormalizedJob;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeterministicJobMatcherTests {

  @Test
  void matchesRelevantUsSoftwareEngineeringRole() {
    MatchResult result =
        DeterministicJobMatcher.match(
            job("Software Engineer", "Remote - United States", "US", "Product Engineering"),
            defaultCriteria());

    assertThat(result.matched()).isTrue();
    assertThat(result.includeKeywordMatches()).containsExactly("software engineer");
    assertThat(result.locationMatches()).contains("US", "remote-us");
    assertThat(result.reasons()).isEmpty();
  }

  @Test
  void treatsRecognizableUsCountryStringsAsUsMatches() {
    List<String> countries =
        List.of("US", "USA", "U.S.", "U.S.A.", "United States", "United States of America");

    for (String country : countries) {
      MatchResult result =
          DeterministicJobMatcher.match(
              job("Software Engineer", "New York", country, "Product Engineering"),
              defaultCriteria());

      assertThat(result.matched()).as(country).isTrue();
      assertThat(result.locationMatches()).as(country).contains("US");
    }
  }

  @Test
  void treatsRemoteUsLocationStringsAsUsMatches() {
    List<String> locations = List.of("Remote - US", "Remote - United States");

    for (String location : locations) {
      MatchResult result =
          DeterministicJobMatcher.match(
              job("Software Engineer", location, null, "Product Engineering"), defaultCriteria());

      assertThat(result.matched()).as(location).isTrue();
      assertThat(result.locationMatches()).as(location).contains("US", "remote-us");
      assertThat(result.reasons()).as(location).isEmpty();
    }
  }

  @Test
  void treatsCityStateLocationsWithUsStateAbbreviationsAsUsMatches() {
    List<String> locations =
        List.of("McLean, VA", "New York, NY", "Atlanta, GA", "San Francisco, CA");

    for (String location : locations) {
      MatchResult result =
          DeterministicJobMatcher.match(
              job("Software Engineer", location, null, "Product Engineering"), defaultCriteria());

      assertThat(result.matched()).as(location).isTrue();
      assertThat(result.locationMatches()).as(location).contains("US");
      assertThat(result.reasons()).as(location).isEmpty();
    }
  }

  @Test
  void rejectsRoleWithoutIncludeKeyword() {
    MatchResult result =
        DeterministicJobMatcher.match(
            job("Account Executive", "Remote - United States", "US", "Sales"), defaultCriteria());

    assertThat(result.matched()).isFalse();
    assertThat(result.reasons()).contains("No include keywords matched.");
  }

  @Test
  void rejectsClearlyTooSeniorRole() {
    MatchResult result =
        DeterministicJobMatcher.match(
            job("Principal Software Engineer", "Remote - United States", "US", "Engineering"),
            defaultCriteria());

    assertThat(result.matched()).isFalse();
    assertThat(result.excludeKeywordMatches()).containsExactly("principal");
  }

  @Test
  void doesNotRejectWhenExcludeKeywordOnlyAppearsInDescription() {
    MatchResult result =
        DeterministicJobMatcher.match(
            job(
                "Software Engineer",
                "Remote - United States",
                "US",
                "Engineering",
                "Build product surfaces and collaborate with product managers."),
            defaultCriteria());

    assertThat(result.matched()).isTrue();
    assertThat(result.excludeKeywordMatches()).isEmpty();
    assertThat(result.reasons()).isEmpty();
  }

  @Test
  void matchesAmbiguousRemoteWithUncertaintyReasonWhenRemoteUsAllowed() {
    MatchResult result =
        DeterministicJobMatcher.match(
            job("Frontend Engineer", "Remote", null, "Engineering"), defaultCriteria());

    assertThat(result.matched()).isTrue();
    assertThat(result.includeKeywordMatches()).containsExactly("frontend");
    assertThat(result.locationMatches()).isEmpty();
    assertThat(result.reasons())
        .contains("Remote location is ambiguous; country could not be confirmed.");
  }

  @Test
  void treatsFrontendSpellingsAsEquivalent() {
    MatchResult frontEndTitle =
        DeterministicJobMatcher.match(
            job("Front-end Engineer", "Remote - United States", "US", "Engineering"),
            frontendOnlyCriteria());
    MatchResult frontEndKeyword =
        DeterministicJobMatcher.match(
            job("Frontend Engineer", "Remote - United States", "US", "Engineering"),
            frontEndOnlyCriteria());
    MatchResult frontEndSpacedKeyword =
        DeterministicJobMatcher.match(
            job("Frontend Engineer", "Remote - United States", "US", "Engineering"),
            frontEndSpacedOnlyCriteria());

    assertThat(frontEndTitle.matched()).isTrue();
    assertThat(frontEndTitle.includeKeywordMatches()).containsExactly("frontend");
    assertThat(frontEndKeyword.matched()).isTrue();
    assertThat(frontEndKeyword.includeKeywordMatches()).containsExactly("front-end");
    assertThat(frontEndSpacedKeyword.matched()).isTrue();
    assertThat(frontEndSpacedKeyword.includeKeywordMatches()).containsExactly("front end");
  }

  @Test
  void rejectsNonUsRoleForUsOnlyCriteria() {
    MatchResult result =
        DeterministicJobMatcher.match(
            job("Software Engineer", "London, UK", "GB", "Engineering"), defaultCriteria());

    assertThat(result.matched()).isFalse();
    assertThat(result.reasons())
        .contains("Location did not match configured country, location, or remote preference.");
  }

  @Test
  void rejectsRemoteRoleWhenOnsiteOnly() {
    JobMatchCriteria onsiteCriteria =
        new JobMatchCriteria(
            List.of("software engineer"),
            List.of(),
            List.of("US"),
            List.of(),
            RemotePreference.ONSITE_ONLY);

    MatchResult result =
        DeterministicJobMatcher.match(
            job("Software Engineer", "Remote - US", "US", "Engineering"), onsiteCriteria);

    assertThat(result.matched()).isFalse();
  }

  private static JobMatchCriteria defaultCriteria() {
    return new JobMatchCriteria(
        List.of("software engineer", "frontend", "full stack"),
        List.of("principal", "staff", "manager"),
        List.of("US"),
        List.of(),
        RemotePreference.REMOTE_US_ALLOWED);
  }

  private static JobMatchCriteria frontendOnlyCriteria() {
    return criteriaWithIncludeKeyword("frontend");
  }

  private static JobMatchCriteria frontEndOnlyCriteria() {
    return criteriaWithIncludeKeyword("front-end");
  }

  private static JobMatchCriteria frontEndSpacedOnlyCriteria() {
    return criteriaWithIncludeKeyword("front end");
  }

  private static JobMatchCriteria criteriaWithIncludeKeyword(String includeKeyword) {
    return new JobMatchCriteria(
        List.of(includeKeyword),
        List.of("principal", "staff", "manager"),
        List.of("US"),
        List.of(),
        RemotePreference.REMOTE_US_ALLOWED);
  }

  private static NormalizedJob job(
      String title, String location, String country, String department) {
    return job(title, location, country, department, "Build production web applications.");
  }

  private static NormalizedJob job(
      String title, String location, String country, String department, String descriptionSnippet) {
    return new NormalizedJob(
        "123",
        title,
        location,
        country,
        "https://example.test/jobs/123",
        null,
        null,
        department,
        "Technology",
        "Mid",
        null,
        descriptionSnippet);
  }
}
