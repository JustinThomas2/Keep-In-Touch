package com.keepintouch.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keepintouch.domain.Contact;
import com.keepintouch.domain.ContactStatus;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

	List<Contact> findByUserIdOrderByFirstNameAscLastNameAsc(UUID userId);

	List<Contact> findByUserIdAndStatusOrderByFirstNameAscLastNameAsc(UUID userId, ContactStatus status);

	List<Contact> findByCompanyIdOrderByFirstNameAscLastNameAsc(UUID companyId);

	Optional<Contact> findByUserIdAndEmailIgnoreCase(UUID userId, String email);
}
