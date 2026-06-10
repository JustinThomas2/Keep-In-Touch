package com.keepintouch.graphql;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest(
    properties = {
      "spring.config.import=optional:file:../.env[.properties]",
      "app.local-user-email=local@keep-in-touch.test"
    })
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

    String companyId =
        graphQlTester
            .document(
                """
				mutation($input: CreateCompanyInput!) {
				  createCompany(input: $input) {
				    id
				    name
				    website
				  }
				}
				""")
            .variable("input", Map.of("name", companyName, "website", "https://example.com"))
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

    String contactId =
        graphQlTester
            .document(
                """
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

    graphQlTester
        .document(
            """
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

    graphQlTester
        .document(
            """
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
        .satisfies(
            contacts ->
                assertThat(contacts)
                    .anySatisfy(
                        contact -> {
                          assertThat(contact.get("id")).isEqualTo(contactId);
                          assertThat(contact.get("email")).isEqualTo(email);
                        }));
  }

  @Test
  void createsAndUpdatesInteractionsThroughGraphql() {
    String suffix = UUID.randomUUID().toString();
    String contactId = createContact("Phase 6 " + suffix, "phase6-" + suffix + "@example.com");

    Map<String, Object> olderInput = new HashMap<>();
    olderInput.put("contactId", contactId);
    olderInput.put("interactionType", "EMAIL");
    olderInput.put("occurredAt", "2026-01-01T14:00:00Z");
    olderInput.put("summary", "Sent an intro email.");
    olderInput.put("outcome", "Waiting for reply.");

    graphQlTester
        .document(
            """
				mutation($input: CreateInteractionInput!) {
				  createInteraction(input: $input) {
				    id
				    contactId
				    interactionType
				    occurredAt
				    summary
				    outcome
				  }
				}
				""")
        .variable("input", olderInput)
        .execute()
        .path("createInteraction.contactId")
        .entity(String.class)
        .isEqualTo(contactId)
        .path("createInteraction.interactionType")
        .entity(String.class)
        .isEqualTo("EMAIL")
        .path("createInteraction.summary")
        .entity(String.class)
        .isEqualTo("Sent an intro email.")
        .path("createInteraction.outcome")
        .entity(String.class)
        .isEqualTo("Waiting for reply.");

    Map<String, Object> newerInput = new HashMap<>();
    newerInput.put("contactId", contactId);
    newerInput.put("interactionType", "COFFEE_CHAT");
    newerInput.put("occurredAt", "2026-02-01T15:30:00Z");
    newerInput.put("summary", "Met for coffee.");

    String newerInteractionId =
        graphQlTester
            .document(
                """
				mutation($input: CreateInteractionInput!) {
				  createInteraction(input: $input) {
				    id
				    occurredAt
				    summary
				  }
				}
				""")
            .variable("input", newerInput)
            .execute()
            .path("createInteraction.occurredAt")
            .entity(String.class)
            .isEqualTo("2026-02-01T15:30Z")
            .path("createInteraction.summary")
            .entity(String.class)
            .isEqualTo("Met for coffee.")
            .path("createInteraction.id")
            .entity(String.class)
            .get();

    graphQlTester
        .document(
            """
				query($contactId: ID!) {
				  contactInteractions(contactId: $contactId) {
				    id
				    occurredAt
				    summary
				  }
				}
				""")
        .variable("contactId", contactId)
        .execute()
        .path("contactInteractions[0].id")
        .entity(String.class)
        .isEqualTo(newerInteractionId)
        .path("contactInteractions[0].summary")
        .entity(String.class)
        .isEqualTo("Met for coffee.");

    graphQlTester
        .document(
            """
				query($id: ID!) {
				  contact(id: $id) {
				    lastInteractionAt
				  }
				}
				""")
        .variable("id", contactId)
        .execute()
        .path("contact.lastInteractionAt")
        .entity(String.class)
        .isEqualTo("2026-02-01T15:30Z");

    Map<String, Object> updateInput = new HashMap<>();
    updateInput.put("id", newerInteractionId);
    updateInput.put("interactionType", "PHONE_CALL");
    updateInput.put("occurredAt", "2025-12-15T12:00:00Z");
    updateInput.put("summary", "Moved this to a phone call.");
    updateInput.put("outcome", "Call completed.");

    graphQlTester
        .document(
            """
				mutation($input: UpdateInteractionInput!) {
				  updateInteraction(input: $input) {
				    id
				    interactionType
				    occurredAt
				    summary
				    outcome
				  }
				}
				""")
        .variable("input", updateInput)
        .execute()
        .path("updateInteraction.id")
        .entity(String.class)
        .isEqualTo(newerInteractionId)
        .path("updateInteraction.interactionType")
        .entity(String.class)
        .isEqualTo("PHONE_CALL")
        .path("updateInteraction.summary")
        .entity(String.class)
        .isEqualTo("Moved this to a phone call.")
        .path("updateInteraction.outcome")
        .entity(String.class)
        .isEqualTo("Call completed.");

    graphQlTester
        .document(
            """
				query($id: ID!) {
				  contact(id: $id) {
				    lastInteractionAt
				  }
				}
				""")
        .variable("id", contactId)
        .execute()
        .path("contact.lastInteractionAt")
        .entity(String.class)
        .isEqualTo("2026-01-01T14:00Z");
  }

  @Test
  void createsCompletesAndShowsFollowUpsThroughGraphql() {
    String suffix = UUID.randomUUID().toString();
    String contactId = createContact("Follow Up " + suffix, "follow-up-" + suffix + "@example.com");
    String overdueAt = OffsetDateTime.now().minusDays(1).withNano(0).toString();

    Map<String, Object> interactionInput = new HashMap<>();
    interactionInput.put("contactId", contactId);
    interactionInput.put("interactionType", "EMAIL");
    interactionInput.put("occurredAt", OffsetDateTime.now().minusDays(2).withNano(0).toString());
    interactionInput.put("summary", "Discussed a follow-up.");

    String interactionId =
        graphQlTester
            .document(
                """
				mutation($input: CreateInteractionInput!) {
				  createInteraction(input: $input) {
				    id
				  }
				}
				""")
            .variable("input", interactionInput)
            .execute()
            .path("createInteraction.id")
            .entity(String.class)
            .get();

    Map<String, Object> followUpInput = new HashMap<>();
    followUpInput.put("contactId", contactId);
    followUpInput.put("interactionId", interactionId);
    followUpInput.put("dueAt", overdueAt);
    followUpInput.put("reason", "Send the promised note.");

    String followUpId =
        graphQlTester
            .document(
                """
				mutation($input: CreateFollowUpInput!) {
				  createFollowUp(input: $input) {
				    id
				    contactId
				    interactionId
				    dueAt
				    status
				    reason
				    contact {
				      id
				      firstName
				    }
				  }
				}
				""")
            .variable("input", followUpInput)
            .execute()
            .path("createFollowUp.contactId")
            .entity(String.class)
            .isEqualTo(contactId)
            .path("createFollowUp.interactionId")
            .entity(String.class)
            .isEqualTo(interactionId)
            .path("createFollowUp.status")
            .entity(String.class)
            .isEqualTo("OPEN")
            .path("createFollowUp.reason")
            .entity(String.class)
            .isEqualTo("Send the promised note.")
            .path("createFollowUp.id")
            .entity(String.class)
            .get();

    graphQlTester
        .document(
            """
				query($id: ID!) {
				  contact(id: $id) {
				    nextFollowUpAt
				  }
				}
				""")
        .variable("id", contactId)
        .execute()
        .path("contact.nextFollowUpAt")
        .entity(String.class)
        .satisfies(
            nextFollowUpAt ->
                assertThat(OffsetDateTime.parse(nextFollowUpAt).toInstant())
                    .isEqualTo(OffsetDateTime.parse(overdueAt).toInstant()));

    graphQlTester
        .document(
            """
				mutation($input: CreateFollowUpInput!) {
				  createFollowUp(input: $input) {
				    id
				  }
				}
				""")
        .variable("input", followUpInput)
        .execute()
        .errors()
        .satisfy(errors -> assertThat(errors).isNotEmpty());

    graphQlTester
        .document(
            """
				query {
				  dashboard {
				    overdueFollowUps {
				      id
				      contact {
				        id
				      }
				    }
				    dueFollowUps {
				      id
				    }
				  }
				}
				""")
        .execute()
        .path("dashboard.overdueFollowUps")
        .entityList(Map.class)
        .satisfies(
            followUps ->
                assertThat(followUps)
                    .anySatisfy(
                        followUp -> {
                          assertThat(followUp.get("id")).isEqualTo(followUpId);
                          assertThat(((Map<?, ?>) followUp.get("contact")).get("id"))
                              .isEqualTo(contactId);
                        }));

    graphQlTester
        .document(
            """
				mutation($id: ID!) {
				  completeFollowUp(id: $id) {
				    id
				    status
				    completedAt
				  }
				}
				""")
        .variable("id", followUpId)
        .execute()
        .path("completeFollowUp.status")
        .entity(String.class)
        .isEqualTo("COMPLETED")
        .path("completeFollowUp.completedAt")
        .entity(String.class)
        .satisfies(completedAt -> assertThat(completedAt).isNotBlank());

    graphQlTester
        .document(
            """
				query($id: ID!) {
				  contact(id: $id) {
				    nextFollowUpAt
				  }
				}
				""")
        .variable("id", contactId)
        .execute()
        .path("contact.nextFollowUpAt")
        .valueIsNull();
  }

  @Test
  void showsDueFollowUpsOnDashboard() {
    String suffix = UUID.randomUUID().toString();
    String contactId =
        createContact("Due Follow Up " + suffix, "due-follow-up-" + suffix + "@example.com");
    String dueAt = OffsetDateTime.now().plusHours(2).withNano(0).toString();

    Map<String, Object> followUpInput = new HashMap<>();
    followUpInput.put("contactId", contactId);
    followUpInput.put("dueAt", dueAt);
    followUpInput.put("reason", "Check in today.");

    String followUpId =
        graphQlTester
            .document(
                """
				mutation($input: CreateFollowUpInput!) {
				  createFollowUp(input: $input) {
				    id
				  }
				}
				""")
            .variable("input", followUpInput)
            .execute()
            .path("createFollowUp.id")
            .entity(String.class)
            .get();

    graphQlTester
        .document(
            """
				query {
				  dashboard {
				    dueFollowUps {
				      id
				      dueAt
				      reason
				    }
				  }
				}
				""")
        .execute()
        .path("dashboard.dueFollowUps")
        .entityList(Map.class)
        .satisfies(
            followUps ->
                assertThat(followUps)
                    .anySatisfy(
                        followUp -> {
                          assertThat(followUp.get("id")).isEqualTo(followUpId);
                          assertThat(followUp.get("reason")).isEqualTo("Check in today.");
                        }));
  }

  @Test
  void cannotCancelCompletedFollowUp() {
    String suffix = UUID.randomUUID().toString();
    String contactId =
        createContact(
            "Completed Follow Up " + suffix, "completed-follow-up-" + suffix + "@example.com");
    String dueAt = OffsetDateTime.now().plusHours(2).withNano(0).toString();

    Map<String, Object> followUpInput = new HashMap<>();
    followUpInput.put("contactId", contactId);
    followUpInput.put("dueAt", dueAt);

    String followUpId =
        graphQlTester
            .document(
                """
				mutation($input: CreateFollowUpInput!) {
				  createFollowUp(input: $input) {
				    id
				  }
				}
				""")
            .variable("input", followUpInput)
            .execute()
            .path("createFollowUp.id")
            .entity(String.class)
            .get();

    graphQlTester
        .document(
            """
				mutation($id: ID!) {
				  completeFollowUp(id: $id) {
				    id
				    status
				  }
				}
				""")
        .variable("id", followUpId)
        .execute()
        .path("completeFollowUp.status")
        .entity(String.class)
        .isEqualTo("COMPLETED");

    graphQlTester
        .document(
            """
				mutation($id: ID!) {
				  cancelFollowUp(id: $id) {
				    id
				  }
				}
				""")
        .variable("id", followUpId)
        .execute()
        .errors()
        .satisfy(errors -> assertThat(errors).isNotEmpty());
  }

  private String createContact(String firstName, String email) {
    Map<String, Object> contactInput = new HashMap<>();
    contactInput.put("firstName", firstName);
    contactInput.put("relationshipType", "PROFESSIONAL");
    contactInput.put("email", email);

    return graphQlTester
        .document(
            """
				mutation($input: CreateContactInput!) {
				  createContact(input: $input) {
				    id
				  }
				}
				""")
        .variable("input", contactInput)
        .execute()
        .path("createContact.id")
        .entity(String.class)
        .get();
  }
}
