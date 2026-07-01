package com.keepintouch.jobwatcher.model;

public record JobSourceFailure(
    WatchedSourceConfig source, String message, String failingUrl, Throwable cause)
    implements JobSourceResult {

  public JobSourceFailure {
    message = message == null || message.isBlank() ? "Unknown source failure." : message.trim();
    failingUrl = failingUrl == null || failingUrl.isBlank() ? null : failingUrl.trim();
  }
}
