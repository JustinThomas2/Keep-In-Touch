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
@Table(name = "contacts")
public class Contact {

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

  @Column(nullable = false, length = 100)
  private String firstName;

  @Column(length = 100)
  private String lastName;

  @Column(length = 100)
  private String preferredName;

  @Column(length = 255)
  private String roleTitle;

  @Column(length = 255)
  private String location;

  @Column(length = 500)
  private String linkedinUrl;

  @Column(length = 255)
  private String email;

  @Column(length = 50)
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private RelationshipType relationshipType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private ContactStatus status = ContactStatus.NEW;

  @Column(length = 255)
  private String source;

  @Column(columnDefinition = "text")
  private String notes;

  private Short birthdayMonth;

  private Short birthdayDay;

  private Integer birthdayYear;

  private OffsetDateTime lastInteractionAt;

  private OffsetDateTime nextFollowUpAt;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false, insertable = false, updatable = false)
  private OffsetDateTime updatedAt;

  protected Contact() {}

  public Contact(User user, String firstName, RelationshipType relationshipType) {
    this.user = user;
    this.firstName = firstName;
    this.relationshipType = relationshipType;
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

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPreferredName() {
    return preferredName;
  }

  public void setPreferredName(String preferredName) {
    this.preferredName = preferredName;
  }

  public String getRoleTitle() {
    return roleTitle;
  }

  public void setRoleTitle(String roleTitle) {
    this.roleTitle = roleTitle;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLinkedinUrl() {
    return linkedinUrl;
  }

  public void setLinkedinUrl(String linkedinUrl) {
    this.linkedinUrl = linkedinUrl;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public RelationshipType getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(RelationshipType relationshipType) {
    this.relationshipType = relationshipType;
  }

  public ContactStatus getStatus() {
    return status;
  }

  public void setStatus(ContactStatus status) {
    this.status = status;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Short getBirthdayMonth() {
    return birthdayMonth;
  }

  public void setBirthdayMonth(Short birthdayMonth) {
    this.birthdayMonth = birthdayMonth;
  }

  public Short getBirthdayDay() {
    return birthdayDay;
  }

  public void setBirthdayDay(Short birthdayDay) {
    this.birthdayDay = birthdayDay;
  }

  public Integer getBirthdayYear() {
    return birthdayYear;
  }

  public void setBirthdayYear(Integer birthdayYear) {
    this.birthdayYear = birthdayYear;
  }

  public OffsetDateTime getLastInteractionAt() {
    return lastInteractionAt;
  }

  public void setLastInteractionAt(OffsetDateTime lastInteractionAt) {
    this.lastInteractionAt = lastInteractionAt;
  }

  public OffsetDateTime getNextFollowUpAt() {
    return nextFollowUpAt;
  }

  public void setNextFollowUpAt(OffsetDateTime nextFollowUpAt) {
    this.nextFollowUpAt = nextFollowUpAt;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
