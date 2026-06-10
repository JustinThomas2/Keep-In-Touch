package com.keepintouch.service;

import com.keepintouch.domain.Contact;
import com.keepintouch.domain.Interaction;
import com.keepintouch.domain.InteractionType;
import com.keepintouch.repository.ContactRepository;
import com.keepintouch.repository.InteractionRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InteractionService {

  private final InteractionRepository interactionRepository;

  private final ContactRepository contactRepository;

  public InteractionService(
      InteractionRepository interactionRepository, ContactRepository contactRepository) {
    this.interactionRepository = interactionRepository;
    this.contactRepository = contactRepository;
  }

  public List<Interaction> findByContactId(UUID contactId) {
    return interactionRepository.findByContactIdOrderByOccurredAtDesc(contactId);
  }

  public Optional<Interaction> findById(UUID id) {
    return interactionRepository.findById(id);
  }

  public Optional<Interaction> findByIdAndUserId(UUID id, UUID userId) {
    return interactionRepository.findByIdAndContactUserId(id, userId);
  }

  @Transactional
  public Interaction save(Interaction interaction) {
    return interactionRepository.save(interaction);
  }

  @Transactional
  public Interaction create(
      UUID userId,
      UUID contactId,
      InteractionType interactionType,
      OffsetDateTime occurredAt,
      String summary,
      String outcome) {
    Contact contact =
        contactRepository
            .findByIdAndUserId(contactId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found."));
    Interaction interaction =
        new Interaction(
            contact,
            requireNonNull(interactionType, "Interaction type is required."),
            requireNonNull(occurredAt, "Occurred at is required."),
            requireText(summary, "Summary is required."));
    interaction.setOutcome(blankToNull(outcome));
    Interaction saved = interactionRepository.save(interaction);
    updateLastInteractionAt(contact);
    return saved;
  }

  @Transactional
  public Interaction update(
      UUID userId,
      UUID interactionId,
      InteractionType interactionType,
      OffsetDateTime occurredAt,
      String summary,
      String outcome) {
    Interaction interaction =
        findByIdAndUserId(interactionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Interaction not found."));
    interaction.setInteractionType(
        requireNonNull(interactionType, "Interaction type is required."));
    interaction.setOccurredAt(requireNonNull(occurredAt, "Occurred at is required."));
    interaction.setSummary(requireText(summary, "Summary is required."));
    interaction.setOutcome(blankToNull(outcome));
    Interaction saved = interactionRepository.save(interaction);
    updateLastInteractionAt(saved.getContact());
    return saved;
  }

  private void updateLastInteractionAt(Contact contact) {
    OffsetDateTime lastInteractionAt =
        interactionRepository
            .findTopByContactIdOrderByOccurredAtDesc(contact.getId())
            .map(Interaction::getOccurredAt)
            .orElse(null);
    contact.setLastInteractionAt(lastInteractionAt);
    contactRepository.save(contact);
  }

  private static String requireText(String value, String message) {
    String normalized = blankToNull(value);
    if (normalized == null) {
      throw new IllegalArgumentException(message);
    }
    return normalized;
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
