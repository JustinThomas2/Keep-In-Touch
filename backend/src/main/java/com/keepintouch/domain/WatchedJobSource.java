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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "watched_job_sources",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uniq_watched_job_sources_id_company_id",
            columnNames = {"id", "company_id"}))
public class WatchedJobSource {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @Column(name = "company_id", insertable = false, updatable = false)
  private UUID companyId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private JobSourceType sourceType;

  @Column(nullable = false, length = 1000)
  private String originalSourceUrl;

  @Column(length = 1000)
  private String canonicalSourceUrl;

  @Column(nullable = false)
  private boolean enabled = true;

  private OffsetDateTime lastCheckedAt;

  private OffsetDateTime lastSuccessfulCheckAt;

  @Column(columnDefinition = "text")
  private String lastError;

  @Column(columnDefinition = "text")
  private String notes;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;

  protected WatchedJobSource() {}

  public WatchedJobSource(Company company, JobSourceType sourceType, String originalSourceUrl) {
    this.company = company;
    this.sourceType = sourceType;
    this.originalSourceUrl = originalSourceUrl;
  }

  public UUID getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public UUID getCompanyId() {
    return companyId;
  }

  public JobSourceType getSourceType() {
    return sourceType;
  }

  public void setSourceType(JobSourceType sourceType) {
    this.sourceType = sourceType;
  }

  public String getOriginalSourceUrl() {
    return originalSourceUrl;
  }

  public void setOriginalSourceUrl(String originalSourceUrl) {
    this.originalSourceUrl = originalSourceUrl;
  }

  public String getCanonicalSourceUrl() {
    return canonicalSourceUrl;
  }

  public void setCanonicalSourceUrl(String canonicalSourceUrl) {
    this.canonicalSourceUrl = canonicalSourceUrl;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public OffsetDateTime getLastCheckedAt() {
    return lastCheckedAt;
  }

  public void setLastCheckedAt(OffsetDateTime lastCheckedAt) {
    this.lastCheckedAt = lastCheckedAt;
  }

  public OffsetDateTime getLastSuccessfulCheckAt() {
    return lastSuccessfulCheckAt;
  }

  public void setLastSuccessfulCheckAt(OffsetDateTime lastSuccessfulCheckAt) {
    this.lastSuccessfulCheckAt = lastSuccessfulCheckAt;
  }

  public String getLastError() {
    return lastError;
  }

  public void setLastError(String lastError) {
    this.lastError = lastError;
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
