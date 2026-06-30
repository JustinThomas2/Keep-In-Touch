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
@Table(name = "job_match_rules")
public class JobMatchRule {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "user_id", insertable = false, updatable = false)
  private UUID userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(
        name = "company_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "user_id",
        insertable = false,
        updatable = false)
  })
  private Company company;

  @Column(name = "company_id")
  private UUID companyId;

  @Column(nullable = false, columnDefinition = "text")
  private String includeKeywords;

  @Column(nullable = false, columnDefinition = "text")
  private String excludeKeywords;

  @Column(nullable = false, columnDefinition = "text")
  private String includeCountries;

  @Column(columnDefinition = "text")
  private String includeLocations;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private RemotePreference remotePreference = RemotePreference.REMOTE_US_ALLOWED;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;

  protected JobMatchRule() {}

  public JobMatchRule(
      User user, String includeKeywords, String excludeKeywords, String includeCountries) {
    this.user = user;
    this.includeKeywords = includeKeywords;
    this.excludeKeywords = excludeKeywords;
    this.includeCountries = includeCountries;
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

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
    this.companyId = company == null ? null : company.getId();
  }

  public UUID getCompanyId() {
    return companyId;
  }

  public String getIncludeKeywords() {
    return includeKeywords;
  }

  public void setIncludeKeywords(String includeKeywords) {
    this.includeKeywords = includeKeywords;
  }

  public String getExcludeKeywords() {
    return excludeKeywords;
  }

  public void setExcludeKeywords(String excludeKeywords) {
    this.excludeKeywords = excludeKeywords;
  }

  public String getIncludeCountries() {
    return includeCountries;
  }

  public void setIncludeCountries(String includeCountries) {
    this.includeCountries = includeCountries;
  }

  public String getIncludeLocations() {
    return includeLocations;
  }

  public void setIncludeLocations(String includeLocations) {
    this.includeLocations = includeLocations;
  }

  public RemotePreference getRemotePreference() {
    return remotePreference;
  }

  public void setRemotePreference(RemotePreference remotePreference) {
    this.remotePreference = remotePreference;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
