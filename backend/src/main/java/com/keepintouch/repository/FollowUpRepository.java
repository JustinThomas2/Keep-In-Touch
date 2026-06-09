package com.keepintouch.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keepintouch.domain.FollowUp;
import com.keepintouch.domain.FollowUpStatus;

public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {

	List<FollowUp> findByContactIdAndStatusOrderByDueAtAsc(UUID contactId, FollowUpStatus status);

	List<FollowUp> findByStatusAndDueAtLessThanEqualOrderByDueAtAsc(FollowUpStatus status, OffsetDateTime dueAt);

	Optional<FollowUp> findByContactIdAndStatus(UUID contactId, FollowUpStatus status);

	List<FollowUp> findByInteractionId(UUID interactionId);
}
