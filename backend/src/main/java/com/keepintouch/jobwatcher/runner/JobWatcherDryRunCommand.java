package com.keepintouch.jobwatcher.runner;

import com.keepintouch.jobwatcher.adapter.ManualFixtureJobSourceAdapter;
import com.keepintouch.jobwatcher.model.JobMatchCriteria;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class JobWatcherDryRunCommand implements ApplicationRunner {

  private final JobWatcherConfigLoader configLoader;
  private final JobWatcherDryRunRunner dryRunRunner;

  public JobWatcherDryRunCommand(
      JobWatcherConfigLoader configLoader, JobWatcherDryRunRunner dryRunRunner) {
    this.configLoader = configLoader;
    this.dryRunRunner = dryRunRunner;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (!JobWatcherCommandLine.isJobWatcherCommand(args)) {
      return;
    }
    if (!args.containsOption("dry-run")) {
      throw new IllegalArgumentException("Only job-watcher --dry-run is implemented.");
    }

    List<WatchedSourceConfig> sources = configLoader.loadEnabledSources();
    Function<WatchedSourceConfig, Optional<JobMatchCriteria>> criteriaLoader =
        configLoader::loadCriteria;
    if (sources.isEmpty()) {
      sources = List.of(ManualFixtureJobSourceAdapter.defaultSource());
      criteriaLoader = source -> Optional.of(configLoader.fixtureDefaultCriteria());
    }
    dryRunRunner.run(sources, criteriaLoader, System.out);
  }
}
