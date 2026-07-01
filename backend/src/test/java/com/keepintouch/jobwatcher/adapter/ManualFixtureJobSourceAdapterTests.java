package com.keepintouch.jobwatcher.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.keepintouch.jobwatcher.model.JobSourceFailure;
import com.keepintouch.jobwatcher.model.JobSourceSuccess;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import org.junit.jupiter.api.Test;

class ManualFixtureJobSourceAdapterTests {

  private final ManualFixtureJobSourceAdapter adapter = new ManualFixtureJobSourceAdapter();

  @Test
  void returnsPredictableFixtureJobs() {
    JobSourceSuccess result =
        (JobSourceSuccess) adapter.fetchAndParse(ManualFixtureJobSourceAdapter.defaultSource());

    assertThat(result.jobs()).hasSize(3);
    assertThat(result.jobs())
        .extracting("externalId")
        .containsExactly("fixture-frontend", "fixture-principal", "fixture-remote-unknown");
  }

  @Test
  void returnsEmptySuccessForEmptyFixtureSource() {
    WatchedSourceConfig source =
        new WatchedSourceConfig(
            ManualFixtureJobSourceAdapter.defaultSource().sourceId(),
            ManualFixtureJobSourceAdapter.defaultSource().companyId(),
            "Fixture Company",
            adapter.sourceType(),
            "fixture://job-watcher/empty",
            null);

    JobSourceSuccess result = (JobSourceSuccess) adapter.fetchAndParse(source);

    assertThat(result.jobs()).isEmpty();
  }

  @Test
  void returnsFailureForFailureFixtureSource() {
    WatchedSourceConfig source =
        new WatchedSourceConfig(
            ManualFixtureJobSourceAdapter.defaultSource().sourceId(),
            ManualFixtureJobSourceAdapter.defaultSource().companyId(),
            "Fixture Company",
            adapter.sourceType(),
            "fixture://job-watcher/failure",
            null);

    JobSourceFailure result = (JobSourceFailure) adapter.fetchAndParse(source);

    assertThat(result.message()).isEqualTo("Manual fixture source was configured to fail.");
    assertThat(result.failingUrl()).isEqualTo("fixture://job-watcher/failure");
  }
}
