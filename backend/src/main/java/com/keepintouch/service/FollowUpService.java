package com.keepintouch.service;

import com.keepintouch.domain.Contact;
import com.keepintouch.domain.FollowUp;
import com.keepintouch.domain.FollowUpStatus;
import com.keepintouch.domain.Interaction;
import com.keepintouch.repository.ContactRepository;
import com.keepintouch.repository.FollowUpRepository;
import com.keepintouch.repository.InteractionRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FollowUpService {

  private final FollowUpRepository followUpRepository;

  private final ContactRepository contactRepository;

  private final InteractionRepository interactionRepository;

  public FollowUpService(
      FollowUpRepository followUpRepository,
      ContactRepository contactRepository,
      InteractionRepository interactionRepository) {
    this.followUpRepository = followUpRepository;
    this.contactRepository = contactRepository;
    this.interactionRepository = interactionRepository;
  }

  public List<FollowUp> findOpenDueBy(OffsetDateTime dueAt) {
    return followUpRepository.findByStatusAndDueAtLessThanEqualOrderByDueAtAsc(
        FollowUpStatus.OPEN, dueAt);
  }

  public Optional<FollowUp> findOpenByContactId(UUID contactId) {
    return followUpRepository.findByContactIdAndStatus(contactId, FollowUpStatus.OPEN);
  }

  public Optional<FollowUp> findOpenByContactIdAndUserId(UUID contactId, UUID userId) {
    if (contactRepository.findByIdAndUserId(contactId, userId).isEmpty()) {
      return Optional.empty();
    }
    return findOpenByContactId(contactId);
  }

  public List<FollowUp> findByContactIdAndStatus(UUID contactId, FollowUpStatus status) {
    return followUpRepository.findByContactIdAndStatusOrderByDueAtAsc(contactId, status);
  }

  public List<FollowUp> findOverdueByUserId(UUID userId, OffsetDateTime now) {
    return followUpRepository.findByContactUserIdAndStatusAndDueAtLessThanOrderByDueAtAsc(
        userId, FollowUpStatus.OPEN, now);
  }

  public List<FollowUp> findDueByUserId(UUID userId, OffsetDateTime now) {
    OffsetDateTime tomorrow = now.plusDays(1);
    return followUpRepository.findByContactUserIdAndStatusAndDueAtBetweenOrderByDueAtAsc(
        userId, FollowUpStatus.OPEN, now, tomorrow);
  }

  @Transactional
  public FollowUp save(FollowUp followUp) {
    return followUpRepository.save(followUp);
  }

  @Transactional
  public FollowUp create(
      UUID userId, UUID contactId, UUID interactionId, OffsetDateTime dueAt, String reason) {
    Contact contact =
        contactRepository
            .findByIdAndUserId(contactId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found."));
    findOpenByContactId(contactId)
        .ifPresent(
            existing -> {
              throw new IllegalArgumentException("Contact already has an open follow-up.");
            });
    FollowUp followUp = new FollowUp(contact, requireNonNull(dueAt, "Due at is required."));
    followUp.setReason(blankToNull(reason));
    if (interactionId != null) {
      Interaction interaction =
          interactionRepository
              .findByIdAndContactUserId(interactionId, userId)
              .filter(candidate -> candidate.getContact().getId().equals(contactId))
              .orElseThrow(
                  () -> new IllegalArgumentException("Interaction not found for contact."));
      followUp.setInteraction(interaction);
    }
    FollowUp saved = followUpRepository.save(followUp);
    updateNextFollowUpAt(contact);
    return saved;
  }

  @Transactional
  public FollowUp complete(UUID userId, UUID followUpId) {
    FollowUp followUp =
        findByIdAndUserId(followUpId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Follow-up not found."));
    requireOpen(followUp);
    followUp.setStatus(FollowUpStatus.COMPLETED);
    followUp.setCompletedAt(OffsetDateTime.now());
    FollowUp saved = followUpRepository.save(followUp);
    updateNextFollowUpAt(saved.getContact());
    return saved;
  }

  @Transactional
  public FollowUp cancel(UUID userId, UUID followUpId) {
    FollowUp followUp =
        findByIdAndUserId(followUpId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Follow-up not found."));
    requireOpen(followUp);
    followUp.setStatus(FollowUpStatus.CANCELLED);
    followUp.setCompletedAt(null);
    FollowUp saved = followUpRepository.save(followUp);
    updateNextFollowUpAt(saved.getContact());
    return saved;
  }

  public Optional<FollowUp> findByIdAndUserId(UUID followUpId, UUID userId) {
    return followUpRepository.findByIdAndContactUserId(followUpId, userId);
  }

  private void updateNextFollowUpAt(Contact contact) {
    OffsetDateTime nextFollowUpAt =
        followUpRepository
            .findTopByContactIdAndStatusOrderByDueAtAsc(contact.getId(), FollowUpStatus.OPEN)
            .map(FollowUp::getDueAt)
            .orElse(null);
    contact.setNextFollowUpAt(nextFollowUpAt);
    contactRepository.save(contact);
  }

  private static void requireOpen(FollowUp followUp) {
    if (followUp.getStatus() != FollowUpStatus.OPEN) {
      throw new IllegalArgumentException("Follow-up is not open.");
    }
  }

  private static <T> T requireNonNull(T value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  private static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
