package com.keepintouch.repository;

import com.keepintouch.domain.JobAlert;
import com.keepintouch.domain.JobAlertChannel;
import com.keepintouch.domain.JobAlertStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobAlertRepository extends JpaRepository<JobAlert, UUID> {

  Optional<JobAlert> findByJobPostingIdAndChannelAndStatus(
      UUID jobPostingId, JobAlertChannel channel, JobAlertStatus status);
}
