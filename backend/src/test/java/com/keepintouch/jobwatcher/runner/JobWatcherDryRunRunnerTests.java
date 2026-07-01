package com.keepintouch.jobwatcher.runner;

import static org.assertj.core.api.Assertions.assertThat;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.domain.RemotePreference;
import com.keepintouch.jobwatcher.adapter.JobSourceAdapter;
import com.keepintouch.jobwatcher.adapter.ManualFixtureJobSourceAdapter;
import com.keepintouch.jobwatcher.model.JobMatchCriteria;
import com.keepintouch.jobwatcher.model.JobSourceFailure;
import com.keepintouch.jobwatcher.model.JobSourceResult;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JobWatcherDryRunRunnerTests {

  @Test
  void dryRunPrintsFoundJobsMatchesAndDiscordPayloadPreviews() {
    JobWatcherDryRunRunner runner =
        new JobWatcherDryRunRunner(List.of(new ManualFixtureJobSourceAdapter()));
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    JobWatcherDryRunRunner.RunSummary summary =
        runner.run(
            List.of(ManualFixtureJobSourceAdapter.defaultSource()),
            source -> Optional.of(defaultCriteria()),
            new PrintStream(output, true, StandardCharsets.UTF_8));

    String text = output.toString(StandardCharsets.UTF_8);
    assertThat(summary.sourcesChecked()).isEqualTo(1);
    assertThat(summary.sourceSuccesses()).isEqualTo(1);
    assertThat(summary.sourceFailures()).isZero();
    assertThat(summary.jobsFound()).isEqualTo(3);
    assertThat(summary.matchedJobs()).isEqualTo(2);
    assertThat(text).contains("Preview only: no database writes and no Discord sends.");
    assertThat(text).contains("Found: Frontend Engineer");
    assertThat(text).contains("Found: Principal Software Engineer");
    assertThat(text).contains("Match: no");
    assertThat(text).contains("Discord payload preview:");
    assertThat(text).contains("Remote location is ambiguous; country could not be confirmed.");
    assertThat(text).contains("Discord sends: 0 (dry-run)");
  }

  @Test
  void sourceFailureDoesNotFailEntireDryRun() {
    JobWatcherDryRunRunner runner =
        new JobWatcherDryRunRunner(
            List.of(new ManualFixtureJobSourceAdapter(), new FailingAdapter()));
    WatchedSourceConfig failingSource =
        new WatchedSourceConfig(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Failing Company",
            JobSourceType.CUSTOM_HTML,
            "https://example.test/failing",
            null);
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    JobWatcherDryRunRunner.RunSummary summary =
        runner.run(
            List.of(failingSource, ManualFixtureJobSourceAdapter.defaultSource()),
            source -> Optional.of(defaultCriteria()),
            new PrintStream(output, true, StandardCharsets.UTF_8));

    String text = output.toString(StandardCharsets.UTF_8);
    assertThat(summary.sourcesChecked()).isEqualTo(2);
    assertThat(summary.sourceSuccesses()).isEqualTo(1);
    assertThat(summary.sourceFailures()).isEqualTo(1);
    assertThat(summary.jobsFound()).isEqualTo(3);
    assertThat(text).contains("Failure: fixture adapter failure");
    assertThat(text).contains("Source: Fixture Company [MANUAL]");
    assertThat(text).contains("Summary");
  }

  @Test
  void skipsMatchingWhenSourceHasNoActiveRule() {
    JobWatcherDryRunRunner runner =
        new JobWatcherDryRunRunner(List.of(new ManualFixtureJobSourceAdapter()));
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    JobWatcherDryRunRunner.RunSummary summary =
        runner.run(
            List.of(ManualFixtureJobSourceAdapter.defaultSource()),
            source -> Optional.empty(),
            new PrintStream(output, true, StandardCharsets.UTF_8));

    String text = output.toString(StandardCharsets.UTF_8);
    assertThat(summary.sourcesChecked()).isEqualTo(1);
    assertThat(summary.sourceSuccesses()).isEqualTo(1);
    assertThat(summary.sourcesSkippedForMissingRules()).isEqualTo(1);
    assertThat(summary.jobsFound()).isEqualTo(3);
    assertThat(summary.matchedJobs()).isZero();
    assertThat(text).contains("Skipped matching: no active job_match_rules found");
    assertThat(text).doesNotContain("Discord payload preview:");
  }

  private static JobMatchCriteria defaultCriteria() {
    return new JobMatchCriteria(
        List.of("software engineer", "frontend", "full stack"),
        List.of("principal", "staff", "manager"),
        List.of("US"),
        List.of(),
        RemotePreference.REMOTE_US_ALLOWED);
  }

  private static class FailingAdapter implements JobSourceAdapter {

    @Override
    public JobSourceType sourceType() {
      return JobSourceType.CUSTOM_HTML;
    }

    @Override
    public JobSourceResult fetchAndParse(WatchedSourceConfig source) {
      return new JobSourceFailure(
          source, "fixture adapter failure", source.effectiveSourceUrl(), null);
    }
  }
}
