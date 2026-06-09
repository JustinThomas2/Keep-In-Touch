package com.keepintouch.graphql;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.keepintouch.domain.Company;
import com.keepintouch.domain.Contact;
import com.keepintouch.domain.ContactStatus;
import com.keepintouch.domain.RelationshipType;
import com.keepintouch.domain.User;
import com.keepintouch.repository.ContactRepository;
import com.keepintouch.service.CompanyService;
import com.keepintouch.service.ContactService;
import com.keepintouch.service.CurrentUserService;

@Controller
public class CompanyContactGraphqlController {

	private final CurrentUserService currentUserService;

	private final CompanyService companyService;

	private final ContactService contactService;

	private final ContactRepository contactRepository;

	public CompanyContactGraphqlController(
			CurrentUserService currentUserService,
			CompanyService companyService,
			ContactService contactService,
			ContactRepository contactRepository) {
		this.currentUserService = currentUserService;
		this.companyService = companyService;
		this.contactService = contactService;
		this.contactRepository = contactRepository;
	}

	@QueryMapping
	public List<CompanyPayload> companies() {
		User user = currentUserService.getCurrentUser();
		return companyService.findByUserId(user.getId()).stream()
				.map(company -> toCompanyPayload(company, false))
				.toList();
	}

	@QueryMapping
	public CompanyPayload company(@Argument UUID id) {
		User user = currentUserService.getCurrentUser();
		return companyService.findByIdAndUserId(id, user.getId())
				.map(company -> toCompanyPayload(company, true))
				.orElse(null);
	}

	@QueryMapping
	public List<ContactPayload> contacts() {
		User user = currentUserService.getCurrentUser();
		return contactService.findByUserId(user.getId()).stream()
				.map(contact -> toContactPayload(contact, true))
				.toList();
	}

	@QueryMapping
	public ContactPayload contact(@Argument UUID id) {
		User user = currentUserService.getCurrentUser();
		return contactService.findByIdAndUserId(id, user.getId())
				.map(contact -> toContactPayload(contact, true))
				.orElse(null);
	}

	@MutationMapping
	public CompanyPayload createCompany(@Argument CreateCompanyInput input) {
		User user = currentUserService.getCurrentUser();
		return toCompanyPayload(companyService.create(user, input.name(), input.website(), input.industry(), input.location(),
				input.notes()), false);
	}

	@MutationMapping
	public CompanyPayload updateCompany(@Argument UpdateCompanyInput input) {
		User user = currentUserService.getCurrentUser();
		return toCompanyPayload(companyService.update(input.id(), user.getId(), input.name(), input.website(),
				input.industry(), input.location(), input.notes()), true);
	}

	@MutationMapping
	public ContactPayload createContact(@Argument CreateContactInput input) {
		User user = currentUserService.getCurrentUser();
		return toContactPayload(contactService.create(user, input.companyId(), input.firstName(), input.lastName(),
				input.preferredName(), input.roleTitle(), input.location(), input.linkedinUrl(), input.email(),
				input.phone(), input.relationshipType(), input.status(), input.source(), input.notes(),
				toShort(input.birthdayMonth()), toShort(input.birthdayDay()), input.birthdayYear()), true);
	}

	@MutationMapping
	public ContactPayload updateContact(@Argument UpdateContactInput input) {
		User user = currentUserService.getCurrentUser();
		return toContactPayload(contactService.update(input.id(), user.getId(), input.companyId(), input.firstName(),
				input.lastName(), input.preferredName(), input.roleTitle(), input.location(), input.linkedinUrl(),
				input.email(), input.phone(), input.relationshipType(), input.status(), input.source(), input.notes(),
				toShort(input.birthdayMonth()), toShort(input.birthdayDay()), input.birthdayYear()), true);
	}

	private CompanyPayload toCompanyPayload(Company company, boolean includeContacts) {
		List<ContactPayload> contacts = includeContacts
				? contactRepository.findByCompanyIdOrderByFirstNameAscLastNameAsc(company.getId()).stream()
						.map(contact -> toContactPayload(contact, false))
						.toList()
				: List.of();
		return new CompanyPayload(
				company.getId(),
				company.getName(),
				company.getWebsite(),
				company.getIndustry(),
				company.getLocation(),
				company.getNotes(),
				format(company.getCreatedAt()),
				format(company.getUpdatedAt()),
				contacts);
	}

	private ContactPayload toContactPayload(Contact contact, boolean includeCompany) {
		CompanyPayload company = includeCompany && contact.getCompany() != null
				? toCompanyPayload(contact.getCompany(), false)
				: null;
		return new ContactPayload(
				contact.getId(),
				contact.getFirstName(),
				contact.getLastName(),
				contact.getPreferredName(),
				contact.getRoleTitle(),
				contact.getLocation(),
				contact.getLinkedinUrl(),
				contact.getEmail(),
				contact.getPhone(),
				contact.getRelationshipType(),
				contact.getStatus(),
				contact.getSource(),
				contact.getNotes(),
				toInteger(contact.getBirthdayMonth()),
				toInteger(contact.getBirthdayDay()),
				contact.getBirthdayYear(),
				format(contact.getLastInteractionAt()),
				format(contact.getNextFollowUpAt()),
				format(contact.getCreatedAt()),
				format(contact.getUpdatedAt()),
				company);
	}

	private static String format(OffsetDateTime value) {
		return value == null ? null : value.toString();
	}

	private static Short toShort(Integer value) {
		return value == null ? null : value.shortValue();
	}

	private static Integer toInteger(Short value) {
		return value == null ? null : value.intValue();
	}

	public record CompanyPayload(
			UUID id,
			String name,
			String website,
			String industry,
			String location,
			String notes,
			String createdAt,
			String updatedAt,
			List<ContactPayload> contacts) {
	}

	public record ContactPayload(
			UUID id,
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
			Integer birthdayMonth,
			Integer birthdayDay,
			Integer birthdayYear,
			String lastInteractionAt,
			String nextFollowUpAt,
			String createdAt,
			String updatedAt,
			CompanyPayload company) {
	}

	public record CreateCompanyInput(
			String name,
			String website,
			String industry,
			String location,
			String notes) {
	}

	public record UpdateCompanyInput(
			UUID id,
			String name,
			String website,
			String industry,
			String location,
			String notes) {
	}

	public record CreateContactInput(
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
			Integer birthdayMonth,
			Integer birthdayDay,
			Integer birthdayYear) {
	}

	public record UpdateContactInput(
			UUID id,
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
			Integer birthdayMonth,
			Integer birthdayDay,
			Integer birthdayYear) {
	}
}
