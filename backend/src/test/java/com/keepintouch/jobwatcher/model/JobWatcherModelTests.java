package com.keepintouch.jobwatcher.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keepintouch.domain.JobSourceType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JobWatcherModelTests {

  @Test
  void sourceConfigUsesCanonicalUrlWhenAvailable() {
    WatchedSourceConfig source =
        new WatchedSourceConfig(
            UUID.randomUUID(),
            UUID.randomUUID(),
            " Capital One ",
            JobSourceType.CAPITAL_ONE_CAREERS,
            "https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1",
            " https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1 ");

    assertThat(source.companyName()).isEqualTo("Capital One");
    assertThat(source.effectiveSourceUrl())
        .isEqualTo("https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1");
  }

  @Test
  void sourceConfigFallsBackToOriginalUrl() {
    WatchedSourceConfig source =
        new WatchedSourceConfig(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Clerk",
            JobSourceType.CLERK_CAREERS,
            "https://clerk.com/careers#open-roles",
            " ");

    assertThat(source.effectiveSourceUrl()).isEqualTo("https://clerk.com/careers#open-roles");
  }

  @Test
  void normalizedJobRequiresTitleAndUrl() {
    assertThatThrownBy(
            () ->
                new NormalizedJob(
                    "123",
                    " ",
                    "Remote - US",
                    "United States",
                    "https://example.test/job/123",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("title must not be blank");
  }

  @Test
  void normalizedJobTrimsFieldsAndNormalizesUsCountry() {
    NormalizedJob job =
        new NormalizedJob(
            " 123 ",
            " Software Engineer ",
            " Remote - US ",
            " united states ",
            " https://example.test/job/123 ",
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    assertThat(job.externalId()).isEqualTo("123");
    assertThat(job.title()).isEqualTo("Software Engineer");
    assertThat(job.location()).isEqualTo("Remote - US");
    assertThat(job.country()).isEqualTo("US");
    assertThat(job.url()).isEqualTo("https://example.test/job/123");
  }
}
