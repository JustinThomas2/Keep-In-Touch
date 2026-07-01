package com.keepintouch.jobwatcher.runner;

import java.util.Arrays;
import org.springframework.boot.ApplicationArguments;

public final class JobWatcherCommandLine {

  public static final String COMMAND_NAME = "job-watcher";

  private JobWatcherCommandLine() {}

  public static boolean isJobWatcherCommand(String[] args) {
    return Arrays.asList(args).contains(COMMAND_NAME);
  }

  public static boolean isJobWatcherCommand(ApplicationArguments args) {
    return args.getNonOptionArgs().contains(COMMAND_NAME);
  }
}
