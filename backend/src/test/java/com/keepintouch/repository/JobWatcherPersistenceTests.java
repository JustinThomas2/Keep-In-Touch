package com.keepintouch.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keepintouch.AbstractPostgresIntegrationTest;
import com.keepintouch.domain.Company;
import com.keepintouch.domain.JobAlert;
import com.keepintouch.domain.JobAlertChannel;
import com.keepintouch.domain.JobAlertStatus;
import com.keepintouch.domain.JobMatchRule;
import com.keepintouch.domain.JobPosting;
import com.keepintouch.domain.JobPostingStatus;
import com.keepintouch.domain.JobSourceType;
import com.keepintouch.domain.RemotePreference;
import com.keepintouch.domain.User;
import com.keepintouch.domain.WatchedJobSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "app.local-user-email=local@keep-in-touch.test")
@Transactional
class JobWatcherPersistenceTests extends AbstractPostgresIntegrationTest {

  private final UserRepository userRepository;

  private final CompanyRepository companyRepository;

  private final WatchedJobSourceRepository watchedJobSourceRepository;

  private final JobPostingRepository jobPostingRepository;

  private final JobMatchRuleRepository jobMatchRuleRepository;

  private final JobAlertRepository jobAlertRepository;

  private final EntityManager entityManager;

  @Autowired
  JobWatcherPersistenceTests(
      UserRepository userRepository,
      CompanyRepository companyRepository,
      WatchedJobSourceRepository watchedJobSourceRepository,
      JobPostingRepository jobPostingRepository,
      JobMatchRuleRepository jobMatchRuleRepository,
      JobAlertRepository jobAlertRepository,
      EntityManager entityManager) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.watchedJobSourceRepository = watchedJobSourceRepository;
    this.jobPostingRepository = jobPostingRepository;
    this.jobMatchRuleRepository = jobMatchRuleRepository;
    this.jobAlertRepository = jobAlertRepository;
    this.entityManager = entityManager;
  }

  @Test
  void savesAndQueriesJobWatcherGraph() {
    User user =
        userRepository.save(
            new User("job-watcher-" + UUID.randomUUID() + "@keep-in-touch.test", "Job Watcher"));
    Company company = companyRepository.save(new Company(user, "Capital One"));

    WatchedJobSource source =
        new WatchedJobSource(
            company,
            JobSourceType.CAPITAL_ONE_CAREERS,
            "https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1");
    source.setCanonicalSourceUrl(source.getOriginalSourceUrl());
    source.setLastCheckedAt(OffsetDateTime.now().minusMinutes(5));
    source.setLastSuccessfulCheckAt(OffsetDateTime.now().minusMinutes(5));
    source = watchedJobSourceRepository.save(source);

    OffsetDateTime seenAt = OffsetDateTime.now();
    JobPosting posting =
        new JobPosting(
            company,
            source,
            "CAPITAL_ONE_CAREERS:96920865664",
            "Software Engineer",
            "https://www.capitalonecareers.com/job/mclean/software-engineer/1732/96920865664",
            seenAt,
            seenAt,
            "content-hash");
    posting.setExternalId("96920865664");
    posting.setLocation("McLean, VA");
    posting.setCountry("US");
    posting.setPostedAt(seenAt.minusDays(1));
    posting = jobPostingRepository.save(posting);

    JobMatchRule rule =
        new JobMatchRule(user, "software engineer\nfrontend", "principal\nstaff", "US");
    rule.setCompany(company);
    rule.setRemotePreference(RemotePreference.REMOTE_US_ALLOWED);
    rule = jobMatchRuleRepository.save(rule);

    JobAlert alert = new JobAlert(posting, JobAlertChannel.DISCORD_WEBHOOK, JobAlertStatus.SENT);
    alert.setSentAt(seenAt.plusMinutes(1));
    alert.setPayloadPreview("New matching role found at Capital One");
    alert = jobAlertRepository.save(alert);

    entityManager.flush();
    entityManager.clear();

    assertThat(watchedJobSourceRepository.findByEnabledTrueOrderByUpdatedAtAsc())
        .extracting(WatchedJobSource::getSourceType)
        .contains(JobSourceType.CAPITAL_ONE_CAREERS);
    assertThat(watchedJobSourceRepository.findByCompanyIdOrderBySourceTypeAsc(company.getId()))
        .extracting(WatchedJobSource::getOriginalSourceUrl)
        .containsExactly(source.getOriginalSourceUrl());

    JobPosting savedPosting =
        jobPostingRepository
            .findBySourceIdAndStableKey(source.getId(), "CAPITAL_ONE_CAREERS:96920865664")
            .orElseThrow();
    assertThat(savedPosting.getId()).isEqualTo(posting.getId());
    assertThat(savedPosting.getStatus()).isEqualTo(JobPostingStatus.ACTIVE);
    assertThat(savedPosting.getCompany().getName()).isEqualTo("Capital One");
    assertThat(savedPosting.getSource().getSourceType())
        .isEqualTo(JobSourceType.CAPITAL_ONE_CAREERS);
    assertThat(savedPosting.getCreatedAt()).isNotNull();
    assertThat(savedPosting.getUpdatedAt()).isNotNull();

    assertThat(
            jobPostingRepository.findByCompanyIdAndStatusOrderByPostedAtDesc(
                company.getId(), JobPostingStatus.ACTIVE))
        .extracting(JobPosting::getStableKey)
        .containsExactly("CAPITAL_ONE_CAREERS:96920865664");

    assertThat(
            jobMatchRuleRepository.findByCompanyIdAndEnabledTrueOrderByCreatedAtAsc(
                company.getId()))
        .extracting(JobMatchRule::getId)
        .containsExactly(rule.getId());

    assertThat(
            jobAlertRepository.findByJobPostingIdAndChannelAndStatus(
                posting.getId(), JobAlertChannel.DISCORD_WEBHOOK, JobAlertStatus.SENT))
        .map(JobAlert::getId)
        .contains(alert.getId());
  }

  @Test
  void rejectsDuplicateStableKeyForSameSource() {
    User user =
        userRepository.save(
            new User("stable-key-" + UUID.randomUUID() + "@keep-in-touch.test", "Stable Key"));
    Company company = companyRepository.save(new Company(user, "Moody's"));
    WatchedJobSource source =
        watchedJobSourceRepository.save(
            new WatchedJobSource(
                company,
                JobSourceType.MOODYS_CAREERS,
                "https://careers.moodys.com/en/search-jobs/software%20engineer/49841/1"));
    OffsetDateTime seenAt = OffsetDateTime.now();

    jobPostingRepository.save(
        new JobPosting(
            company,
            source,
            "MOODYS_CAREERS:example",
            "Software Engineer",
            "https://careers.moodys.com/en/job/example",
            seenAt,
            seenAt,
            "hash-one"));
    entityManager.flush();

    jobPostingRepository.save(
        new JobPosting(
            company,
            source,
            "MOODYS_CAREERS:example",
            "Software Engineer",
            "https://careers.moodys.com/en/job/example",
            seenAt,
            seenAt,
            "hash-two"));

    assertThatThrownBy(entityManager::flush)
        .isInstanceOfAny(DataIntegrityViolationException.class, PersistenceException.class);
  }

  @Test
  void rejectsDuplicateSentDiscordAlertForSameJob() {
    User user =
        userRepository.save(
            new User("sent-alert-" + UUID.randomUUID() + "@keep-in-touch.test", "Sent Alert"));
    Company company = companyRepository.save(new Company(user, "Clerk"));
    WatchedJobSource source =
        watchedJobSourceRepository.save(
            new WatchedJobSource(
                company, JobSourceType.CLERK_ASHBY, "https://clerk.com/careers#open-roles"));
    OffsetDateTime seenAt = OffsetDateTime.now();
    JobPosting posting =
        jobPostingRepository.save(
            new JobPosting(
                company,
                source,
                "CLERK_ASHBY:example",
                "Frontend Engineer",
                "https://jobs.ashbyhq.com/Clerk/example",
                seenAt,
                seenAt,
                "hash"));

    JobAlert firstAlert =
        new JobAlert(posting, JobAlertChannel.DISCORD_WEBHOOK, JobAlertStatus.SENT);
    firstAlert.setSentAt(seenAt.plusMinutes(1));
    jobAlertRepository.save(firstAlert);
    entityManager.flush();

    JobAlert duplicateAlert =
        new JobAlert(posting, JobAlertChannel.DISCORD_WEBHOOK, JobAlertStatus.SENT);
    duplicateAlert.setSentAt(seenAt.plusMinutes(2));
    jobAlertRepository.save(duplicateAlert);

    assertThatThrownBy(entityManager::flush)
        .isInstanceOfAny(DataIntegrityViolationException.class, PersistenceException.class);
  }
}
