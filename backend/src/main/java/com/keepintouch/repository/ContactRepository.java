package com.keepintouch.repository;

import com.keepintouch.domain.Contact;
import com.keepintouch.domain.ContactStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

  List<Contact> findByUserIdOrderByFirstNameAscLastNameAsc(UUID userId);

  List<Contact> findByUserIdAndStatusOrderByFirstNameAscLastNameAsc(
      UUID userId, ContactStatus status);

  List<Contact> findByUserIdAndBirthdayMonthIsNotNullAndBirthdayDayIsNotNull(UUID userId);

  List<Contact> findByCompanyIdOrderByFirstNameAscLastNameAsc(UUID companyId);

  Optional<Contact> findByIdAndUserId(UUID id, UUID userId);

  Optional<Contact> findByUserIdAndEmailIgnoreCase(UUID userId, String email);
}
