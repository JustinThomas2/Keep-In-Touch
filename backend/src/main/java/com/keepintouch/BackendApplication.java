package com.keepintouch;

import com.keepintouch.jobwatcher.runner.JobWatcherCommandLine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(BackendApplication.class);
    if (JobWatcherCommandLine.isJobWatcherCommand(args)) {
      application.setWebApplicationType(WebApplicationType.NONE);
    }
    application.run(args);
  }
}
