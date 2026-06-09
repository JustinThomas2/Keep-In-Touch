package com.keepintouch.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keepintouch.domain.Contact;
import com.keepintouch.domain.ContactStatus;
import com.keepintouch.repository.ContactRepository;

@Service
@Transactional(readOnly = true)
public class ContactService {

	private final ContactRepository contactRepository;

	public ContactService(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
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

	@Transactional
	public Contact save(Contact contact) {
		return contactRepository.save(contact);
	}
}
