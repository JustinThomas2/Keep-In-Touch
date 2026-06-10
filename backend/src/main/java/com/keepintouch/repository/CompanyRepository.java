package com.keepintouch.repository;

import com.keepintouch.domain.Company;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

  List<Company> findByUserIdOrderByNameAsc(UUID userId);

  Optional<Company> findByIdAndUserId(UUID id, UUID userId);
}
