package com.keepintouch.jobwatcher.runner;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

class JobWatcherCommandLineTests {

  @Test
  void detectsJobWatcherCommandWithoutDependingOnPosition() {
    assertThat(JobWatcherCommandLine.isJobWatcherCommand(new String[] {"job-watcher", "--dry-run"}))
        .isTrue();
    assertThat(JobWatcherCommandLine.isJobWatcherCommand(new String[] {"--dry-run", "job-watcher"}))
        .isTrue();
  }

  @Test
  void ignoresNonCommandArguments() {
    assertThat(JobWatcherCommandLine.isJobWatcherCommand(new String[] {"--name=job-watcher"}))
        .isFalse();
    assertThat(JobWatcherCommandLine.isJobWatcherCommand(new String[] {"--dry-run"})).isFalse();
  }

  @Test
  void detectsCommandFromSpringApplicationArguments() {
    DefaultApplicationArguments args =
        new DefaultApplicationArguments(new String[] {"--dry-run", "job-watcher"});

    assertThat(JobWatcherCommandLine.isJobWatcherCommand(args)).isTrue();
  }
}
