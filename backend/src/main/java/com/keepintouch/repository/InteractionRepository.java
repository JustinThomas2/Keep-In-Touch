package com.keepintouch.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keepintouch.domain.Interaction;

public interface InteractionRepository extends JpaRepository<Interaction, UUID> {

	List<Interaction> findByContactIdOrderByOccurredAtDesc(UUID contactId);

	Optional<Interaction> findByIdAndContactUserId(UUID id, UUID userId);

	Optional<Interaction> findTopByContactIdOrderByOccurredAtDesc(UUID contactId);
}
