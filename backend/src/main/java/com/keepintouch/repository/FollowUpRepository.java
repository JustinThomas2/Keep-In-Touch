package com.keepintouch.repository;

import com.keepintouch.domain.FollowUp;
import com.keepintouch.domain.FollowUpStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {

  List<FollowUp> findByContactIdAndStatusOrderByDueAtAsc(UUID contactId, FollowUpStatus status);

  List<FollowUp> findByStatusAndDueAtLessThanEqualOrderByDueAtAsc(
      FollowUpStatus status, OffsetDateTime dueAt);

  @EntityGraph(attributePaths = {"contact", "contact.company"})
  Optional<FollowUp> findByContactIdAndStatus(UUID contactId, FollowUpStatus status);

  @EntityGraph(attributePaths = {"contact", "contact.company"})
  Optional<FollowUp> findByIdAndContactUserId(UUID id, UUID userId);

  @EntityGraph(attributePaths = {"contact", "contact.company"})
  List<FollowUp> findByContactUserIdAndStatusAndDueAtLessThanOrderByDueAtAsc(
      UUID userId, FollowUpStatus status, OffsetDateTime dueAt);

  @EntityGraph(attributePaths = {"contact", "contact.company"})
  List<FollowUp> findByContactUserIdAndStatusAndDueAtBetweenOrderByDueAtAsc(
      UUID userId, FollowUpStatus status, OffsetDateTime start, OffsetDateTime end);

  Optional<FollowUp> findTopByContactIdAndStatusOrderByDueAtAsc(
      UUID contactId, FollowUpStatus status);

  List<FollowUp> findByInteractionId(UUID interactionId);
}
