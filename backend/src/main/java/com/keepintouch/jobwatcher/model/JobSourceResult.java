package com.keepintouch.jobwatcher.model;

public sealed interface JobSourceResult permits JobSourceSuccess, JobSourceFailure {

  WatchedSourceConfig source();
}
