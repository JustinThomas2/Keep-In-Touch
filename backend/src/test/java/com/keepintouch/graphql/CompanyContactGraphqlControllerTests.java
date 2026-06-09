package com.keepintouch.graphql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest(properties = "app.local-user-email=local@keep-in-touch.test")
@AutoConfigureGraphQlTester
class CompanyContactGraphqlControllerTests {

	private final GraphQlTester graphQlTester;

	@Autowired
	CompanyContactGraphqlControllerTests(GraphQlTester graphQlTester) {
		this.graphQlTester = graphQlTester;
	}

	@Test
	void createsCompanyAndContactThroughGraphql() {
		String suffix = UUID.randomUUID().toString();
		String companyName = "Phase 5 Company " + suffix;
		String email = "phase5-" + suffix + "@example.com";

		String companyId = graphQlTester.document("""
				mutation($input: CreateCompanyInput!) {
				  createCompany(input: $input) {
				    id
				    name
				    website
				  }
				}
				""")
				.variable("input", Map.of(
						"name", companyName,
						"website", "https://example.com"))
				.execute()
				.path("createCompany.name")
				.entity(String.class)
				.isEqualTo(companyName)
				.path("createCompany.website")
				.entity(String.class)
				.isEqualTo("https://example.com")
				.path("createCompany.id")
				.entity(String.class)
				.get();

		Map<String, Object> contactInput = new HashMap<>();
		contactInput.put("companyId", companyId);
		contactInput.put("firstName", "Ada");
		contactInput.put("relationshipType", "PROFESSIONAL");
		contactInput.put("email", email);
		contactInput.put("birthdayMonth", 2);
		contactInput.put("birthdayDay", 29);
		contactInput.put("birthdayYear", 1996);

		String contactId = graphQlTester.document("""
				mutation($input: CreateContactInput!) {
				  createContact(input: $input) {
				    id
				    firstName
				    email
				    relationshipType
				    status
				    birthdayMonth
				    birthdayDay
				    birthdayYear
				    company {
				      id
				      name
				    }
				  }
				}
				""")
				.variable("input", contactInput)
				.execute()
				.path("createContact.firstName")
				.entity(String.class)
				.isEqualTo("Ada")
				.path("createContact.email")
				.entity(String.class)
				.isEqualTo(email)
				.path("createContact.relationshipType")
				.entity(String.class)
				.isEqualTo("PROFESSIONAL")
				.path("createContact.status")
				.entity(String.class)
				.isEqualTo("NEW")
				.path("createContact.birthdayMonth")
				.entity(Integer.class)
				.isEqualTo(2)
				.path("createContact.birthdayDay")
				.entity(Integer.class)
				.isEqualTo(29)
				.path("createContact.birthdayYear")
				.entity(Integer.class)
				.isEqualTo(1996)
				.path("createContact.company.id")
				.entity(String.class)
				.satisfies(id -> assertThat(id).isEqualTo(companyId))
				.path("createContact.company.name")
				.entity(String.class)
				.isEqualTo(companyName)
				.path("createContact.id")
				.entity(String.class)
				.get();

		graphQlTester.document("""
				query($id: ID!) {
				  contact(id: $id) {
				    id
				    firstName
				    email
				    company {
				      id
				      name
				    }
				  }
				}
				""")
				.variable("id", contactId)
				.execute()
				.path("contact.id")
				.entity(String.class)
				.isEqualTo(contactId)
				.path("contact.company.id")
				.entity(String.class)
				.isEqualTo(companyId)
				.path("contact.company.name")
				.entity(String.class)
				.isEqualTo(companyName);

		graphQlTester.document("""
				query($id: ID!) {
				  company(id: $id) {
				    id
				    name
				    contacts {
				      id
				      firstName
				      email
				    }
				  }
				}
				""")
				.variable("id", companyId)
				.execute()
				.path("company.id")
				.entity(String.class)
				.isEqualTo(companyId)
				.path("company.contacts")
				.entityList(Map.class)
				.satisfies(contacts -> assertThat(contacts)
						.anySatisfy(contact -> {
							assertThat(contact.get("id")).isEqualTo(contactId);
							assertThat(contact.get("email")).isEqualTo(email);
						}));
	}
}
