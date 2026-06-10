package com.keepintouch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "follow_ups")
public class FollowUp {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contact_id", nullable = false)
  private Contact contact;

  @Column(name = "contact_id", insertable = false, updatable = false)
  private UUID contactId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(
        name = "interaction_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "contact_id",
        referencedColumnName = "contact_id",
        insertable = false,
        updatable = false)
  })
  private Interaction interaction;

  @Column(name = "interaction_id")
  private UUID interactionId;

  @Column(nullable = false)
  private OffsetDateTime dueAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private FollowUpStatus status = FollowUpStatus.OPEN;

  @Column(columnDefinition = "text")
  private String reason;

  private OffsetDateTime completedAt;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;

  protected FollowUp() {}

  public FollowUp(Contact contact, OffsetDateTime dueAt) {
    this.contact = contact;
    this.dueAt = dueAt;
  }

  public UUID getId() {
    return id;
  }

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public UUID getContactId() {
    return contactId;
  }

  public Interaction getInteraction() {
    return interaction;
  }

  public void setInteraction(Interaction interaction) {
    this.interaction = interaction;
    this.interactionId = interaction == null ? null : interaction.getId();
  }

  public UUID getInteractionId() {
    return interactionId;
  }

  public OffsetDateTime getDueAt() {
    return dueAt;
  }

  public void setDueAt(OffsetDateTime dueAt) {
    this.dueAt = dueAt;
  }

  public FollowUpStatus getStatus() {
    return status;
  }

  public void setStatus(FollowUpStatus status) {
    this.status = status;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public OffsetDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(OffsetDateTime completedAt) {
    this.completedAt = completedAt;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
