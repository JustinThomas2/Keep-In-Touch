package com.keepintouch.jobwatcher.runner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.domain.RemotePreference;
import com.keepintouch.jobwatcher.adapter.ManualFixtureJobSourceAdapter;
import com.keepintouch.jobwatcher.model.JobMatchCriteria;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.DefaultApplicationArguments;

class JobWatcherDryRunCommandTests {

  @Test
  void usesFixtureDefaultCriteriaOnlyWhenNoWatchedSourcesExist() {
    JobWatcherConfigLoader configLoader = org.mockito.Mockito.mock(JobWatcherConfigLoader.class);
    JobWatcherDryRunRunner dryRunRunner = org.mockito.Mockito.mock(JobWatcherDryRunRunner.class);
    JobMatchCriteria fixtureCriteria = criteria("frontend");
    when(configLoader.loadEnabledSources()).thenReturn(List.of());
    when(configLoader.fixtureDefaultCriteria()).thenReturn(fixtureCriteria);
    when(dryRunRunner.run(anyList(), any(), any(PrintStream.class)))
        .thenReturn(new JobWatcherDryRunRunner.RunSummary());

    new JobWatcherDryRunCommand(configLoader, dryRunRunner)
        .run(new DefaultApplicationArguments("job-watcher", "--dry-run"));

    CapturedRun captured = captureRun(dryRunRunner);
    assertThat(captured.sources()).containsExactly(ManualFixtureJobSourceAdapter.defaultSource());
    assertThat(captured.criteriaLoader().apply(ManualFixtureJobSourceAdapter.defaultSource()))
        .containsSame(fixtureCriteria);
  }

  @Test
  void realWatchedSourcesUseDatabaseCriteriaWithoutDefaultFallback() {
    JobWatcherConfigLoader configLoader = org.mockito.Mockito.mock(JobWatcherConfigLoader.class);
    JobWatcherDryRunRunner dryRunRunner = org.mockito.Mockito.mock(JobWatcherDryRunRunner.class);
    WatchedSourceConfig realSource =
        new WatchedSourceConfig(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Capital One",
            JobSourceType.CAPITAL_ONE_CAREERS,
            "https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1",
            null);
    when(configLoader.loadEnabledSources()).thenReturn(List.of(realSource));
    when(configLoader.loadCriteria(realSource)).thenReturn(Optional.empty());
    when(dryRunRunner.run(anyList(), any(), any(PrintStream.class)))
        .thenReturn(new JobWatcherDryRunRunner.RunSummary());

    new JobWatcherDryRunCommand(configLoader, dryRunRunner)
        .run(new DefaultApplicationArguments("job-watcher", "--dry-run"));

    CapturedRun captured = captureRun(dryRunRunner);
    assertThat(captured.sources()).containsExactly(realSource);
    assertThat(captured.criteriaLoader().apply(realSource)).isEmpty();
    verify(configLoader, never()).fixtureDefaultCriteria();
  }

  @Test
  void ignoresNonJobWatcherCommands() {
    JobWatcherConfigLoader configLoader = org.mockito.Mockito.mock(JobWatcherConfigLoader.class);
    JobWatcherDryRunRunner dryRunRunner = org.mockito.Mockito.mock(JobWatcherDryRunRunner.class);

    new JobWatcherDryRunCommand(configLoader, dryRunRunner)
        .run(new DefaultApplicationArguments("--dry-run"));

    verify(dryRunRunner, never()).run(anyList(), any(), same(System.out));
  }

  @SuppressWarnings("unchecked")
  private static CapturedRun captureRun(JobWatcherDryRunRunner dryRunRunner) {
    ArgumentCaptor<List<WatchedSourceConfig>> sourcesCaptor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<Function<WatchedSourceConfig, Optional<JobMatchCriteria>>> criteriaCaptor =
        ArgumentCaptor.forClass(Function.class);
    verify(dryRunRunner).run(sourcesCaptor.capture(), criteriaCaptor.capture(), same(System.out));
    return new CapturedRun(sourcesCaptor.getValue(), criteriaCaptor.getValue());
  }

  private static JobMatchCriteria criteria(String includeKeyword) {
    return new JobMatchCriteria(
        List.of(includeKeyword),
        List.of("principal"),
        List.of("US"),
        List.of(),
        RemotePreference.REMOTE_US_ALLOWED);
  }

  private record CapturedRun(
      List<WatchedSourceConfig> sources,
      Function<WatchedSourceConfig, Optional<JobMatchCriteria>> criteriaLoader) {}
}
