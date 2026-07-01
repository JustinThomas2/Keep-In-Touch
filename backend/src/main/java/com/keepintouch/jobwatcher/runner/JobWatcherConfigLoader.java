package com.keepintouch.jobwatcher.runner;

import com.keepintouch.domain.JobMatchRule;
import com.keepintouch.domain.RemotePreference;
import com.keepintouch.domain.WatchedJobSource;
import com.keepintouch.jobwatcher.model.JobMatchCriteria;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import com.keepintouch.repository.JobMatchRuleRepository;
import com.keepintouch.repository.WatchedJobSourceRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JobWatcherConfigLoader {

  private static final JobMatchCriteria DEFAULT_CRITERIA =
      new JobMatchCriteria(
          List.of(
              "software engineer",
              "software developer",
              "frontend",
              "full stack",
              "react",
              "typescript",
              "javascript",
              "product engineer",
              "application engineer",
              "internal tools",
              "platform engineer"),
          List.of(
              "staff",
              "principal",
              "distinguished",
              "director",
              "manager",
              "mobile-only",
              "ios",
              "android",
              "embedded",
              "firmware",
              "devops",
              "sre",
              "data scientist",
              "machine learning engineer",
              "internship",
              "new grad"),
          List.of("US"),
          List.of(),
          RemotePreference.REMOTE_US_ALLOWED);

  private final WatchedJobSourceRepository watchedJobSourceRepository;
  private final JobMatchRuleRepository jobMatchRuleRepository;

  public JobWatcherConfigLoader(
      WatchedJobSourceRepository watchedJobSourceRepository,
      JobMatchRuleRepository jobMatchRuleRepository) {
    this.watchedJobSourceRepository = watchedJobSourceRepository;
    this.jobMatchRuleRepository = jobMatchRuleRepository;
  }

  @Transactional(readOnly = true)
  public List<WatchedSourceConfig> loadEnabledSources() {
    return watchedJobSourceRepository.findByEnabledTrueOrderByUpdatedAtAsc().stream()
        .map(this::toSourceConfig)
        .toList();
  }

  @Transactional(readOnly = true)
  public Optional<JobMatchCriteria> loadCriteria(WatchedSourceConfig source) {
    Optional<JobMatchCriteria> companyRule =
        jobMatchRuleRepository
            .findByCompanyIdAndEnabledTrueOrderByCreatedAtAsc(source.companyId())
            .stream()
            .findFirst()
            .map(this::toCriteria);
    if (companyRule.isPresent() || source.userId() == null) {
      return companyRule;
    }
    return jobMatchRuleRepository
        .findByUserIdAndCompanyIdIsNullAndEnabledTrueOrderByCreatedAtAsc(source.userId())
        .stream()
        .findFirst()
        .map(this::toCriteria);
  }

  public JobMatchCriteria fixtureDefaultCriteria() {
    return DEFAULT_CRITERIA;
  }

  private WatchedSourceConfig toSourceConfig(WatchedJobSource source) {
    return new WatchedSourceConfig(
        source.getId(),
        source.getCompanyId(),
        source.getCompany().getUserId(),
        source.getCompany().getName(),
        source.getSourceType(),
        source.getOriginalSourceUrl(),
        source.getCanonicalSourceUrl());
  }

  private JobMatchCriteria toCriteria(JobMatchRule rule) {
    return new JobMatchCriteria(
        splitLines(rule.getIncludeKeywords()),
        splitLines(rule.getExcludeKeywords()),
        splitLines(rule.getIncludeCountries()),
        splitLines(rule.getIncludeLocations()),
        rule.getRemotePreference());
  }

  private static List<String> splitLines(String value) {
    if (value == null || value.isBlank()) {
      return List.of();
    }
    return Arrays.stream(value.split("\\R")).map(String::trim).filter(s -> !s.isEmpty()).toList();
  }
}
