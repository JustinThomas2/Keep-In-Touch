package com.keepintouch.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "companies", uniqueConstraints = @UniqueConstraint(name = "uniq_companies_id_user_id", columnNames = {
		"id", "user_id" }))
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "user_id", insertable = false, updatable = false)
	private UUID userId;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(length = 500)
	private String website;

	@Column(length = 255)
	private String industry;

	@Column(length = 255)
	private String location;

	@Column(columnDefinition = "text")
	private String notes;

	@Column(nullable = false, insertable = false, updatable = false)
	private OffsetDateTime createdAt;

	@Column(nullable = false, insertable = false, updatable = false)
	private OffsetDateTime updatedAt;

	protected Company() {
	}

	public Company(User user, String name) {
		this.user = user;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}
}
