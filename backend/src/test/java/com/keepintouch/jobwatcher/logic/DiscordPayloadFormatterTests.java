package com.keepintouch.jobwatcher.logic;

import static org.assertj.core.api.Assertions.assertThat;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.jobwatcher.model.DiscordPayload;
import com.keepintouch.jobwatcher.model.MatchResult;
import com.keepintouch.jobwatcher.model.NormalizedJob;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DiscordPayloadFormatterTests {

  @Test
  void formatsMatchingJobAsDiscordWebhookPayload() {
    WatchedSourceConfig source =
        new WatchedSourceConfig(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Clerk",
            JobSourceType.CLERK_ASHBY,
            "https://clerk.com/careers#open-roles",
            "https://jobs.ashbyhq.com/clerk");
    NormalizedJob job =
        new NormalizedJob(
            "frontend-engineer",
            "Frontend Engineer",
            "Remote - US",
            "US",
            "https://jobs.ashbyhq.com/clerk/frontend-engineer",
            null,
            "https://jobs.ashbyhq.com/clerk/frontend-engineer/application",
            "Engineering",
            "Product",
            "Mid",
            null,
            "Build developer-facing UI.");
    MatchResult matchResult = MatchResult.matched(List.of("frontend"), List.of("US"));

    DiscordPayload payload = DiscordPayloadFormatter.formatMatch(source, job, matchResult);

    assertThat(payload.content()).isEqualTo("New matching role found at Clerk");
    assertThat(payload.embeds()).hasSize(1);
    assertThat(payload.embeds().getFirst().title()).isEqualTo("Frontend Engineer");
    assertThat(payload.embeds().getFirst().url())
        .isEqualTo("https://jobs.ashbyhq.com/clerk/frontend-engineer");
    assertThat(payload.embeds().getFirst().fields())
        .extracting("name")
        .containsExactly("Company", "Location", "Department", "Matched", "Apply");
    assertThat(payload.embeds().getFirst().fields())
        .extracting("value")
        .contains(
            "Clerk",
            "Remote - US",
            "Engineering",
            "frontend",
            "https://jobs.ashbyhq.com/clerk/frontend-engineer/application");
  }
}
