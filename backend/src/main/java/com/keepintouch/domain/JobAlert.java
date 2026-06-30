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
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_alerts")
public class JobAlert {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_posting_id", nullable = false)
  private JobPosting jobPosting;

  @Column(name = "job_posting_id", insertable = false, updatable = false)
  private UUID jobPostingId;

  private OffsetDateTime sentAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private JobAlertChannel channel;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private JobAlertStatus status;

  @Column(columnDefinition = "text")
  private String errorMessage;

  @Column(columnDefinition = "text")
  private String payloadPreview;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;

  protected JobAlert() {}

  public JobAlert(JobPosting jobPosting, JobAlertChannel channel, JobAlertStatus status) {
    this.jobPosting = jobPosting;
    this.channel = channel;
    this.status = status;
  }

  public UUID getId() {
    return id;
  }

  public JobPosting getJobPosting() {
    return jobPosting;
  }

  public void setJobPosting(JobPosting jobPosting) {
    this.jobPosting = jobPosting;
  }

  public UUID getJobPostingId() {
    return jobPostingId;
  }

  public OffsetDateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(OffsetDateTime sentAt) {
    this.sentAt = sentAt;
  }

  public JobAlertChannel getChannel() {
    return channel;
  }

  public void setChannel(JobAlertChannel channel) {
    this.channel = channel;
  }

  public JobAlertStatus getStatus() {
    return status;
  }

  public void setStatus(JobAlertStatus status) {
    this.status = status;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getPayloadPreview() {
    return payloadPreview;
  }

  public void setPayloadPreview(String payloadPreview) {
    this.payloadPreview = payloadPreview;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
