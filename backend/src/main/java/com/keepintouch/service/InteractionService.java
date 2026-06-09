package com.keepintouch.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keepintouch.domain.Interaction;
import com.keepintouch.repository.InteractionRepository;

@Service
@Transactional(readOnly = true)
public class InteractionService {

	private final InteractionRepository interactionRepository;

	public InteractionService(InteractionRepository interactionRepository) {
		this.interactionRepository = interactionRepository;
	}

	public List<Interaction> findByContactId(UUID contactId) {
		return interactionRepository.findByContactIdOrderByOccurredAtDesc(contactId);
	}

	public Optional<Interaction> findById(UUID id) {
		return interactionRepository.findById(id);
	}

	@Transactional
	public Interaction save(Interaction interaction) {
		return interactionRepository.save(interaction);
	}
}
