package com.keepintouch.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "interactions", uniqueConstraints = @UniqueConstraint(name = "uniq_interactions_id_contact_id", columnNames = {
		"id", "contact_id" }))
public class Interaction {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "contact_id", nullable = false)
	private Contact contact;

	@Column(name = "contact_id", insertable = false, updatable = false)
	private UUID contactId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private InteractionType interactionType;

	@Column(nullable = false)
	private OffsetDateTime occurredAt;

	@Column(nullable = false, columnDefinition = "text")
	private String summary;

	@Column(columnDefinition = "text")
	private String outcome;

	@Column(nullable = false, insertable = false, updatable = false)
	private OffsetDateTime createdAt;

	@Column(nullable = false, insertable = false, updatable = false)
	private OffsetDateTime updatedAt;

	protected Interaction() {
	}

	public Interaction(Contact contact, InteractionType interactionType, OffsetDateTime occurredAt, String summary) {
		this.contact = contact;
		this.interactionType = interactionType;
		this.occurredAt = occurredAt;
		this.summary = summary;
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

	public InteractionType getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(InteractionType interactionType) {
		this.interactionType = interactionType;
	}

	public OffsetDateTime getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(OffsetDateTime occurredAt) {
		this.occurredAt = occurredAt;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}
}
