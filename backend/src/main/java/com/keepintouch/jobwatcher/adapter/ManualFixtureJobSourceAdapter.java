package com.keepintouch.jobwatcher.adapter;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.jobwatcher.model.JobSourceFailure;
import com.keepintouch.jobwatcher.model.JobSourceResult;
import com.keepintouch.jobwatcher.model.JobSourceSuccess;
import com.keepintouch.jobwatcher.model.NormalizedJob;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ManualFixtureJobSourceAdapter implements JobSourceAdapter {

  public static WatchedSourceConfig defaultSource() {
    return new WatchedSourceConfig(
        UUID.nameUUIDFromBytes("fixture-source".getBytes()),
        UUID.nameUUIDFromBytes("fixture-company".getBytes()),
        "Fixture Company",
        JobSourceType.MANUAL,
        "fixture://job-watcher/manual",
        null);
  }

  @Override
  public JobSourceType sourceType() {
    return JobSourceType.MANUAL;
  }

  @Override
  public JobSourceResult fetchAndParse(WatchedSourceConfig source) {
    if (source.originalSourceUrl().contains("failure")) {
      return new JobSourceFailure(
          source,
          "Manual fixture source was configured to fail.",
          source.effectiveSourceUrl(),
          null);
    }
    if (source.originalSourceUrl().contains("empty")) {
      return new JobSourceSuccess(source, List.of(), source.effectiveSourceUrl());
    }
    return new JobSourceSuccess(source, fixtureJobs(), source.effectiveSourceUrl());
  }

  private static List<NormalizedJob> fixtureJobs() {
    return List.of(
        new NormalizedJob(
            "fixture-frontend",
            "Frontend Engineer",
            "Remote - United States",
            "US",
            "https://example.test/jobs/frontend-engineer",
            null,
            "https://example.test/jobs/frontend-engineer/apply",
            "Engineering",
            "Product Engineering",
            "Mid",
            OffsetDateTime.parse("2026-06-01T13:00:00Z"),
            "Build product UI with React and TypeScript."),
        new NormalizedJob(
            "fixture-principal",
            "Principal Software Engineer",
            "Remote - United States",
            "US",
            "https://example.test/jobs/principal-software-engineer",
            null,
            null,
            "Engineering",
            "Platform",
            "Principal",
            OffsetDateTime.parse("2026-06-02T13:00:00Z"),
            "Lead platform architecture for product teams."),
        new NormalizedJob(
            "fixture-remote-unknown",
            "Full Stack Engineer",
            "Remote",
            null,
            "https://example.test/jobs/full-stack-engineer",
            null,
            null,
            "Engineering",
            "Product Engineering",
            "Mid",
            OffsetDateTime.parse("2026-06-03T13:00:00Z"),
            "Build web applications across frontend and backend systems."));
  }
}
