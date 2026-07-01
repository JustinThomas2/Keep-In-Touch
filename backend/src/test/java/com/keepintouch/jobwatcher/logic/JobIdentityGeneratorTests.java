package com.keepintouch.jobwatcher.logic;

import static org.assertj.core.api.Assertions.assertThat;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.jobwatcher.model.NormalizedJob;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JobIdentityGeneratorTests {

  @Test
  void stableKeyUsesNormalizedExternalIdWhenAvailable() {
    String stableKey =
        JobIdentityGenerator.stableKey(
            source(JobSourceType.CAPITAL_ONE_CAREERS), job(" 96920865664 "));

    assertThat(stableKey).isEqualTo("CAPITAL_ONE_CAREERS:96920865664");
  }

  @Test
  void stableKeyFallsBackToNormalizedCanonicalUrlHash() {
    NormalizedJob first =
        new NormalizedJob(
            null,
            "Software Engineer",
            "McLean, VA",
            "US",
            "https://Example.test/jobs/123/#details",
            "https://Example.test/jobs/123/",
            null,
            null,
            null,
            null,
            null,
            null);
    NormalizedJob second =
        new NormalizedJob(
            null,
            "Software Engineer",
            "McLean, VA",
            "US",
            "https://example.test/jobs/123",
            "https://example.test/jobs/123",
            null,
            null,
            null,
            null,
            null,
            null);

    assertThat(JobIdentityGenerator.stableKey(source(JobSourceType.MOODYS_CAREERS), first))
        .isEqualTo(JobIdentityGenerator.stableKey(source(JobSourceType.MOODYS_CAREERS), second))
        .startsWith("MOODYS_CAREERS:url:");
  }

  @Test
  void contentHashIsStableForEquivalentWhitespaceAndUrlCase() {
    NormalizedJob first = job("123");
    NormalizedJob second =
        new NormalizedJob(
            "123",
            " Software   Engineer ",
            " Remote - US ",
            "USA",
            "https://EXAMPLE.test/jobs/123/",
            null,
            null,
            " Engineering ",
            " Technology ",
            " Mid ",
            OffsetDateTime.parse("2026-06-01T09:00:00-04:00"),
            " Build customer-facing systems. ");

    assertThat(JobIdentityGenerator.contentHash(first))
        .isEqualTo(JobIdentityGenerator.contentHash(second))
        .hasSize(64);
  }

  @Test
  void contentHashChangesWhenContentChanges() {
    NormalizedJob first = job("123");
    NormalizedJob changed =
        new NormalizedJob(
            "123",
            "Senior Software Engineer",
            "Remote - US",
            "US",
            "https://example.test/jobs/123",
            null,
            null,
            "Engineering",
            "Technology",
            "Mid",
            OffsetDateTime.parse("2026-06-01T13:00:00Z"),
            "Build customer-facing systems.");

    assertThat(JobIdentityGenerator.contentHash(first))
        .isNotEqualTo(JobIdentityGenerator.contentHash(changed));
  }

  private static WatchedSourceConfig source(JobSourceType sourceType) {
    return new WatchedSourceConfig(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "Example",
        sourceType,
        "https://example.test/jobs",
        null);
  }

  private static NormalizedJob job(String externalId) {
    return new NormalizedJob(
        externalId,
        "Software Engineer",
        "Remote - US",
        "US",
        "https://example.test/jobs/123",
        null,
        null,
        "Engineering",
        "Technology",
        "Mid",
        OffsetDateTime.parse("2026-06-01T13:00:00Z"),
        "Build customer-facing systems.");
  }
}
