package com.keepintouch.service;

import com.keepintouch.domain.Contact;
import com.keepintouch.domain.ContactStatus;
import com.keepintouch.domain.RelationshipType;
import com.keepintouch.domain.User;
import com.keepintouch.repository.CompanyRepository;
import com.keepintouch.repository.ContactRepository;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContactService {

  private final ContactRepository contactRepository;

  private final CompanyRepository companyRepository;

  public ContactService(ContactRepository contactRepository, CompanyRepository companyRepository) {
    this.contactRepository = contactRepository;
    this.companyRepository = companyRepository;
  }

  public List<Contact> findByUserId(UUID userId) {
    return contactRepository.findByUserIdOrderByFirstNameAscLastNameAsc(userId);
  }

  public List<Contact> findByUserIdAndStatus(UUID userId, ContactStatus status) {
    return contactRepository.findByUserIdAndStatusOrderByFirstNameAscLastNameAsc(userId, status);
  }

  public Optional<Contact> findById(UUID id) {
    return contactRepository.findById(id);
  }

  public Optional<Contact> findByIdAndUserId(UUID id, UUID userId) {
    return contactRepository.findByIdAndUserId(id, userId);
  }

  public List<Contact> findUpcomingBirthdays(UUID userId, LocalDate today, int daysAhead) {
    LocalDate end = today.plusDays(daysAhead);
    return contactRepository
        .findByUserIdAndBirthdayMonthIsNotNullAndBirthdayDayIsNotNull(userId)
        .stream()
        .filter(contact -> !nextBirthday(contact, today).isAfter(end))
        .sorted(Comparator.comparing(contact -> nextBirthday(contact, today)))
        .toList();
  }

  @Transactional
  public Contact save(Contact contact) {
    validateUniqueEmail(contact);
    return contactRepository.save(contact);
  }

  @Transactional
  public Contact create(
      User user,
      UUID companyId,
      String firstName,
      String lastName,
      String preferredName,
      String roleTitle,
      String location,
      String linkedinUrl,
      String email,
      String phone,
      RelationshipType relationshipType,
      ContactStatus status,
      String source,
      String notes,
      Short birthdayMonth,
      Short birthdayDay,
      Integer birthdayYear) {
    Contact contact =
        new Contact(
            user,
            requireText(firstName, "First name is required."),
            requireNonNull(relationshipType, "Relationship type is required."));
    applyFields(
        contact,
        companyId,
        lastName,
        preferredName,
        roleTitle,
        location,
        linkedinUrl,
        email,
        phone,
        status == null ? ContactStatus.NEW : status,
        source,
        notes,
        birthdayMonth,
        birthdayDay,
        birthdayYear);
    validateUniqueEmail(contact);
    return contactRepository.save(contact);
  }

  @Transactional
  public Contact update(
      UUID contactId,
      UUID userId,
      UUID companyId,
      String firstName,
      String lastName,
      String preferredName,
      String roleTitle,
      String location,
      String linkedinUrl,
      String email,
      String phone,
      RelationshipType relationshipType,
      ContactStatus status,
      String source,
      String notes,
      Short birthdayMonth,
      Short birthdayDay,
      Integer birthdayYear) {
    Contact contact =
        findByIdAndUserId(contactId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found."));
    contact.setFirstName(requireText(firstName, "First name is required."));
    contact.setRelationshipType(requireNonNull(relationshipType, "Relationship type is required."));
    applyFields(
        contact,
        companyId,
        lastName,
        preferredName,
        roleTitle,
        location,
        linkedinUrl,
        email,
        phone,
        status == null ? ContactStatus.NEW : status,
        source,
        notes,
        birthdayMonth,
        birthdayDay,
        birthdayYear);
    validateUniqueEmail(contact);
    return contactRepository.save(contact);
  }

  private void applyFields(
      Contact contact,
      UUID companyId,
      String lastName,
      String preferredName,
      String roleTitle,
      String location,
      String linkedinUrl,
      String email,
      String phone,
      ContactStatus status,
      String source,
      String notes,
      Short birthdayMonth,
      Short birthdayDay,
      Integer birthdayYear) {
    if (companyId == null) {
      contact.setCompany(null);
    } else {
      UUID userId = contact.getUser().getId();
      contact.setCompany(
          companyRepository
              .findByIdAndUserId(companyId, userId)
              .orElseThrow(() -> new IllegalArgumentException("Company not found.")));
    }
    contact.setLastName(blankToNull(lastName));
    contact.setPreferredName(blankToNull(preferredName));
    contact.setRoleTitle(blankToNull(roleTitle));
    contact.setLocation(blankToNull(location));
    contact.setLinkedinUrl(blankToNull(linkedinUrl));
    contact.setEmail(blankToNull(email));
    contact.setPhone(blankToNull(phone));
    contact.setStatus(requireNonNull(status, "Contact status is required."));
    contact.setSource(blankToNull(source));
    contact.setNotes(blankToNull(notes));
    validateBirthday(birthdayMonth, birthdayDay, birthdayYear);
    contact.setBirthdayMonth(birthdayMonth);
    contact.setBirthdayDay(birthdayDay);
    contact.setBirthdayYear(birthdayYear);
  }

  private static LocalDate nextBirthday(Contact contact, LocalDate today) {
    LocalDate birthday =
        birthdayInYear(contact.getBirthdayMonth(), contact.getBirthdayDay(), today.getYear());
    if (birthday.isBefore(today)) {
      return birthdayInYear(
          contact.getBirthdayMonth(), contact.getBirthdayDay(), today.getYear() + 1);
    }
    return birthday;
  }

  private static LocalDate birthdayInYear(Short month, Short day, int year) {
    if (month == 2 && day == 29 && !java.time.Year.isLeap(year)) {
      return LocalDate.of(year, 2, 28);
    }
    return MonthDay.of(month, day).atYear(year);
  }

  private static void validateBirthday(Short month, Short day, Integer year) {
    if ((month == null) != (day == null)) {
      throw new IllegalArgumentException("Birthday month and day must be set together.");
    }
    if (year != null && month == null) {
      throw new IllegalArgumentException("Birthday year requires month and day.");
    }
    if (month == null) {
      return;
    }
    if (month < 1 || month > 12) {
      throw new IllegalArgumentException("Birthday month must be between 1 and 12.");
    }
    if (day < 1 || day > 31) {
      throw new IllegalArgumentException("Birthday day must be between 1 and 31.");
    }
    if (year == null) {
      MonthDay.of(month, day);
      return;
    }
    LocalDate.of(year, month, day);
  }

  private void validateUniqueEmail(Contact contact) {
    String email = blankToNull(contact.getEmail());
    if (email == null) {
      return;
    }
    contactRepository
        .findByUserIdAndEmailIgnoreCase(contact.getUser().getId(), email)
        .filter(existing -> !existing.getId().equals(contact.getId()))
        .ifPresent(
            existing -> {
              throw new IllegalArgumentException("A contact with this email already exists.");
            });
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
