package com.keepintouch.repository;

import com.keepintouch.domain.WatchedJobSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchedJobSourceRepository extends JpaRepository<WatchedJobSource, UUID> {

  List<WatchedJobSource> findByCompanyIdOrderBySourceTypeAsc(UUID companyId);

  List<WatchedJobSource> findByEnabledTrueOrderByUpdatedAtAsc();

  Optional<WatchedJobSource> findByIdAndCompanyUserId(UUID id, UUID userId);
}
