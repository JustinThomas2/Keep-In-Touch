package com.keepintouch.repository;

import com.keepintouch.domain.JobPosting;
import com.keepintouch.domain.JobPostingStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, UUID> {

  List<JobPosting> findBySourceIdOrderByLastSeenAtDesc(UUID sourceId);

  List<JobPosting> findByCompanyIdAndStatusOrderByPostedAtDesc(
      UUID companyId, JobPostingStatus status);

  Optional<JobPosting> findBySourceIdAndStableKey(UUID sourceId, String stableKey);

  @EntityGraph(attributePaths = {"company", "source"})
  Optional<JobPosting> findByIdAndCompanyUserId(UUID id, UUID userId);
}
