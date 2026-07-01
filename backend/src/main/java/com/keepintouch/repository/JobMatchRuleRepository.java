package com.keepintouch.repository;

import com.keepintouch.domain.JobMatchRule;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobMatchRuleRepository extends JpaRepository<JobMatchRule, UUID> {

  List<JobMatchRule> findByUserIdAndEnabledTrueOrderByCreatedAtAsc(UUID userId);

  List<JobMatchRule> findByUserIdAndCompanyIdIsNullAndEnabledTrueOrderByCreatedAtAsc(UUID userId);

  List<JobMatchRule> findByCompanyIdAndEnabledTrueOrderByCreatedAtAsc(UUID companyId);
}
