package com.keepintouch.jobwatcher.adapter;

import com.keepintouch.domain.JobSourceType;
import com.keepintouch.jobwatcher.model.JobSourceResult;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;

public interface JobSourceAdapter {

  JobSourceType sourceType();

  JobSourceResult fetchAndParse(WatchedSourceConfig source);
}
