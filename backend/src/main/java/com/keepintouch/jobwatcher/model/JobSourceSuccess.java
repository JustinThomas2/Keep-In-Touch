package com.keepintouch.jobwatcher.model;

import java.util.List;

public record JobSourceSuccess(
    WatchedSourceConfig source, List<NormalizedJob> jobs, String canonicalSourceUrl)
    implements JobSourceResult {

  public JobSourceSuccess {
    jobs = List.copyOf(jobs);
    canonicalSourceUrl =
        canonicalSourceUrl == null || canonicalSourceUrl.isBlank()
            ? null
            : canonicalSourceUrl.trim();
  }
}
