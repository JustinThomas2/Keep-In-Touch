package com.keepintouch.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keepintouch.domain.Company;
import com.keepintouch.domain.User;
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

	public Optional<Company> findByIdAndUserId(UUID id, UUID userId) {
		return companyRepository.findByIdAndUserId(id, userId);
	}

	@Transactional
	public Company save(Company company) {
		validate(company);
		return companyRepository.save(company);
	}

	@Transactional
	public Company create(User user, String name, String website, String industry, String location, String notes) {
		Company company = new Company(user, requireText(name, "Company name is required."));
		company.setWebsite(blankToNull(website));
		company.setIndustry(blankToNull(industry));
		company.setLocation(blankToNull(location));
		company.setNotes(blankToNull(notes));
		return companyRepository.save(company);
	}

	@Transactional
	public Company update(UUID companyId, UUID userId, String name, String website, String industry, String location,
			String notes) {
		Company company = findByIdAndUserId(companyId, userId)
				.orElseThrow(() -> new IllegalArgumentException("Company not found."));
		company.setName(requireText(name, "Company name is required."));
		company.setWebsite(blankToNull(website));
		company.setIndustry(blankToNull(industry));
		company.setLocation(blankToNull(location));
		company.setNotes(blankToNull(notes));
		return companyRepository.save(company);
	}

	private void validate(Company company) {
		requireText(company.getName(), "Company name is required.");
	}

	private static String requireText(String value, String message) {
		String normalized = blankToNull(value);
		if (normalized == null) {
			throw new IllegalArgumentException(message);
		}
		return normalized;
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
