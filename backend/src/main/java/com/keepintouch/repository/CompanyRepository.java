package com.keepintouch.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keepintouch.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

	List<Company> findByUserIdOrderByNameAsc(UUID userId);
}
