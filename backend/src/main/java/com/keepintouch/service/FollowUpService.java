package com.keepintouch.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keepintouch.domain.FollowUp;
import com.keepintouch.domain.FollowUpStatus;
import com.keepintouch.repository.FollowUpRepository;

@Service
@Transactional(readOnly = true)
public class FollowUpService {

	private final FollowUpRepository followUpRepository;

	public FollowUpService(FollowUpRepository followUpRepository) {
		this.followUpRepository = followUpRepository;
	}

	public List<FollowUp> findOpenDueBy(OffsetDateTime dueAt) {
		return followUpRepository.findByStatusAndDueAtLessThanEqualOrderByDueAtAsc(FollowUpStatus.OPEN, dueAt);
	}

	public Optional<FollowUp> findOpenByContactId(UUID contactId) {
		return followUpRepository.findByContactIdAndStatus(contactId, FollowUpStatus.OPEN);
	}

	public List<FollowUp> findByContactIdAndStatus(UUID contactId, FollowUpStatus status) {
		return followUpRepository.findByContactIdAndStatusOrderByDueAtAsc(contactId, status);
	}

	@Transactional
	public FollowUp save(FollowUp followUp) {
		return followUpRepository.save(followUp);
	}
}
