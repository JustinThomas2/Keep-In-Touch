package com.keepintouch.jobwatcher.runner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.keepintouch.domain.JobMatchRule;
import com.keepintouch.domain.JobSourceType;
import com.keepintouch.domain.RemotePreference;
import com.keepintouch.domain.User;
import com.keepintouch.jobwatcher.model.JobMatchCriteria;
import com.keepintouch.jobwatcher.model.WatchedSourceConfig;
import com.keepintouch.repository.JobMatchRuleRepository;
import com.keepintouch.repository.WatchedJobSourceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JobWatcherConfigLoaderTests {

  private final WatchedJobSourceRepository watchedJobSourceRepository =
      org.mockito.Mockito.mock(WatchedJobSourceRepository.class);

  private final JobMatchRuleRepository jobMatchRuleRepository =
      org.mockito.Mockito.mock(JobMatchRuleRepository.class);

  private final JobWatcherConfigLoader configLoader =
      new JobWatcherConfigLoader(watchedJobSourceRepository, jobMatchRuleRepository);

  @Test
  void companySpecificRuleTakesPrecedenceOverGlobalRule() {
    WatchedSourceConfig source = sourceConfig();
    JobMatchRule companyRule = rule("frontend", "principal", "US");
    when(jobMatchRuleRepository.findByCompanyIdAndEnabledTrueOrderByCreatedAtAsc(
            source.companyId()))
        .thenReturn(List.of(companyRule));

    Optional<JobMatchCriteria> criteria = configLoader.loadCriteria(source);

    assertThat(criteria).isPresent();
    assertThat(criteria.orElseThrow().includeKeywords()).containsExactly("frontend");
    verify(jobMatchRuleRepository, never())
        .findByUserIdAndCompanyIdIsNullAndEnabledTrueOrderByCreatedAtAsc(source.userId());
  }

  @Test
  void globalUserRuleIsUsedWhenCompanySpecificRuleDoesNotExist() {
    WatchedSourceConfig source = sourceConfig();
    JobMatchRule globalRule = rule("software engineer\nfull stack", "staff", "US");
    when(jobMatchRuleRepository.findByCompanyIdAndEnabledTrueOrderByCreatedAtAsc(
            source.companyId()))
        .thenReturn(List.of());
    when(jobMatchRuleRepository.findByUserIdAndCompanyIdIsNullAndEnabledTrueOrderByCreatedAtAsc(
            source.userId()))
        .thenReturn(List.of(globalRule));

    Optional<JobMatchCriteria> criteria = configLoader.loadCriteria(source);

    assertThat(criteria).isPresent();
    assertThat(criteria.orElseThrow().includeKeywords())
        .containsExactly("software engineer", "full stack");
    assertThat(criteria.orElseThrow().excludeKeywords()).containsExactly("staff");
  }

  @Test
  void missingCompanyAndGlobalRulesReturnsEmptyCriteria() {
    WatchedSourceConfig source = sourceConfig();
    when(jobMatchRuleRepository.findByCompanyIdAndEnabledTrueOrderByCreatedAtAsc(
            source.companyId()))
        .thenReturn(List.of());
    when(jobMatchRuleRepository.findByUserIdAndCompanyIdIsNullAndEnabledTrueOrderByCreatedAtAsc(
            source.userId()))
        .thenReturn(List.of());

    assertThat(configLoader.loadCriteria(source)).isEmpty();
  }

  private static WatchedSourceConfig sourceConfig() {
    return new WatchedSourceConfig(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        "Capital One",
        JobSourceType.CAPITAL_ONE_CAREERS,
        "https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1",
        null);
  }

  private static JobMatchRule rule(
      String includeKeywords, String excludeKeywords, String includeCountries) {
    JobMatchRule rule =
        new JobMatchRule(
            new User("rule-" + UUID.randomUUID() + "@example.test", "Rule User"),
            includeKeywords,
            excludeKeywords,
            includeCountries);
    rule.setRemotePreference(RemotePreference.REMOTE_US_ALLOWED);
    return rule;
  }
}
