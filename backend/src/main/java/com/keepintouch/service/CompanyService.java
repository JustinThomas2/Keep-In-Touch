package com.keepintouch.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keepintouch.domain.Company;
import com.keepintouch.repository.CompanyRepository;

@Service
@Transactional(readOnly = true)
public class CompanyService {

	private final CompanyRepository companyRepository;

	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	public List<Company> findByUserId(UUID userId) {
		return companyRepository.findByUserIdOrderByNameAsc(userId);
	}

	public Optional<Company> findById(UUID id) {
		return companyRepository.findById(id);
	}

	@Transactional
	public Company save(Company company) {
		return companyRepository.save(company);
	}
}
