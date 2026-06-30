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
@Table(name = "job_postings")
public class JobPosting {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @Column(name = "company_id", insertable = false, updatable = false)
  private UUID companyId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumns({
    @JoinColumn(
        name = "source_id",
        referencedColumnName = "id",
        nullable = false,
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "company_id",
        referencedColumnName = "company_id",
        nullable = false,
        insertable = false,
        updatable = false)
  })
  private WatchedJobSource source;

  @Column(name = "source_id", nullable = false)
  private UUID sourceId;

  @Column(length = 255)
  private String externalId;

  @Column(nullable = false, length = 1000)
  private String stableKey;

  @Column(nullable = false, length = 500)
  private String title;

  @Column(length = 500)
  private String location;

  @Column(length = 100)
  private String country;

  @Column(nullable = false, length = 1000)
  private String url;

  @Column(length = 1000)
  private String canonicalUrl;

  @Column(length = 1000)
  private String applyUrl;

  @Column(length = 255)
  private String department;

  @Column(length = 255)
  private String jobCategory;

  @Column(length = 255)
  private String experienceLevel;

  private OffsetDateTime postedAt;

  @Column(columnDefinition = "text")
  private String descriptionSnippet;

  @Column(nullable = false)
  private OffsetDateTime firstSeenAt;

  @Column(nullable = false)
  private OffsetDateTime lastSeenAt;

  @Column(nullable = false, length = 128)
  private String contentHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private JobPostingStatus status = JobPostingStatus.ACTIVE;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;

  protected JobPosting() {}

  public JobPosting(
      Company company,
      WatchedJobSource source,
      String stableKey,
      String title,
      String url,
      OffsetDateTime firstSeenAt,
      OffsetDateTime lastSeenAt,
      String contentHash) {
    this.company = company;
    this.source = source;
    this.sourceId = source.getId();
    this.stableKey = stableKey;
    this.title = title;
    this.url = url;
    this.firstSeenAt = firstSeenAt;
    this.lastSeenAt = lastSeenAt;
    this.contentHash = contentHash;
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

  public WatchedJobSource getSource() {
    return source;
  }

  public void setSource(WatchedJobSource source) {
    this.source = source;
    this.sourceId = source == null ? null : source.getId();
  }

  public UUID getSourceId() {
    return sourceId;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getStableKey() {
    return stableKey;
  }

  public void setStableKey(String stableKey) {
    this.stableKey = stableKey;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getCanonicalUrl() {
    return canonicalUrl;
  }

  public void setCanonicalUrl(String canonicalUrl) {
    this.canonicalUrl = canonicalUrl;
  }

  public String getApplyUrl() {
    return applyUrl;
  }

  public void setApplyUrl(String applyUrl) {
    this.applyUrl = applyUrl;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getJobCategory() {
    return jobCategory;
  }

  public void setJobCategory(String jobCategory) {
    this.jobCategory = jobCategory;
  }

  public String getExperienceLevel() {
    return experienceLevel;
  }

  public void setExperienceLevel(String experienceLevel) {
    this.experienceLevel = experienceLevel;
  }

  public OffsetDateTime getPostedAt() {
    return postedAt;
  }

  public void setPostedAt(OffsetDateTime postedAt) {
    this.postedAt = postedAt;
  }

  public String getDescriptionSnippet() {
    return descriptionSnippet;
  }

  public void setDescriptionSnippet(String descriptionSnippet) {
    this.descriptionSnippet = descriptionSnippet;
  }

  public OffsetDateTime getFirstSeenAt() {
    return firstSeenAt;
  }

  public void setFirstSeenAt(OffsetDateTime firstSeenAt) {
    this.firstSeenAt = firstSeenAt;
  }

  public OffsetDateTime getLastSeenAt() {
    return lastSeenAt;
  }

  public void setLastSeenAt(OffsetDateTime lastSeenAt) {
    this.lastSeenAt = lastSeenAt;
  }

  public String getContentHash() {
    return contentHash;
  }

  public void setContentHash(String contentHash) {
    this.contentHash = contentHash;
  }

  public JobPostingStatus getStatus() {
    return status;
  }

  public void setStatus(JobPostingStatus status) {
    this.status = status;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
