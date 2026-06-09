package com.keepintouch.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keepintouch.domain.Contact;
import com.keepintouch.domain.ContactStatus;
import com.keepintouch.domain.RelationshipType;
import com.keepintouch.domain.User;
import com.keepintouch.repository.CompanyRepository;
import com.keepintouch.repository.ContactRepository;

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
		Contact contact = new Contact(user, requireText(firstName, "First name is required."), requireNonNull(relationshipType,
				"Relationship type is required."));
		applyFields(contact, companyId, lastName, preferredName, roleTitle, location, linkedinUrl, email, phone,
				status == null ? ContactStatus.NEW : status, source, notes, birthdayMonth, birthdayDay, birthdayYear);
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
		Contact contact = findByIdAndUserId(contactId, userId)
				.orElseThrow(() -> new IllegalArgumentException("Contact not found."));
		contact.setFirstName(requireText(firstName, "First name is required."));
		contact.setRelationshipType(requireNonNull(relationshipType, "Relationship type is required."));
		applyFields(contact, companyId, lastName, preferredName, roleTitle, location, linkedinUrl, email, phone,
				status == null ? ContactStatus.NEW : status, source, notes, birthdayMonth, birthdayDay, birthdayYear);
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
		}
		else {
			UUID userId = contact.getUser().getId();
			contact.setCompany(companyRepository.findByIdAndUserId(companyId, userId)
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
		contact.setBirthdayMonth(birthdayMonth);
		contact.setBirthdayDay(birthdayDay);
		contact.setBirthdayYear(birthdayYear);
	}

	private void validateUniqueEmail(Contact contact) {
		String email = blankToNull(contact.getEmail());
		if (email == null) {
			return;
		}
		contactRepository.findByUserIdAndEmailIgnoreCase(contact.getUser().getId(), email)
				.filter(existing -> !existing.getId().equals(contact.getId()))
				.ifPresent(existing -> {
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
