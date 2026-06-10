package com.keepintouch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keepintouch.AbstractPostgresIntegrationTest;
import com.keepintouch.domain.Contact;
import com.keepintouch.domain.ContactStatus;
import com.keepintouch.domain.FollowUp;
import com.keepintouch.domain.FollowUpStatus;
import com.keepintouch.domain.Interaction;
import com.keepintouch.domain.InteractionType;
import com.keepintouch.domain.RelationshipType;
import com.keepintouch.domain.User;
import com.keepintouch.repository.ContactRepository;
import com.keepintouch.repository.UserRepository;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.local-user-email=local@keep-in-touch.test")
class ContactWorkflowServiceTests extends AbstractPostgresIntegrationTest {

  @Autowired private ContactService contactService;

  @Autowired private InteractionService interactionService;

  @Autowired private FollowUpService followUpService;

  @Autowired private ContactRepository contactRepository;

  @Autowired private UserRepository userRepository;

  @Test
  void createsContactWithRequiredFieldsAndOptionalFieldsOmitted() {
    User user = createUser();

    Contact contact = createContact(user, "Ada");

    assertThat(contact.getId()).isNotNull();
    assertThat(contact.getFirstName()).isEqualTo("Ada");
    assertThat(contact.getLastName()).isNull();
    assertThat(contact.getEmail()).isNull();
    assertThat(contact.getPhone()).isNull();
    assertThat(contact.getLinkedinUrl()).isNull();
    assertThat(contact.getStatus()).isEqualTo(ContactStatus.NEW);
  }

  @Test
  void rejectsDuplicateEmailPerUserWhenEmailIsPresent() {
    User user = createUser();
    createContact(user, "Ada", "Lovelace", "ada@example.test");

    assertThatThrownBy(() -> createContact(user, "Grace", "Hopper", "ADA@example.test"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("A contact with this email already exists.");
  }

  @Test
  void createsAndUpdatesInteractionAndRefreshesLastInteractionAt() {
    User user = createUser();
    Contact contact = createContact(user, "Ada", "Lovelace", null);
    OffsetDateTime firstOccurredAt = OffsetDateTime.parse("2026-01-10T12:00:00Z");
    OffsetDateTime updatedOccurredAt = OffsetDateTime.parse("2026-01-12T14:30:00Z");

    Interaction interaction =
        interactionService.create(
            user.getId(),
            contact.getId(),
            InteractionType.EMAIL,
            firstOccurredAt,
            "Reached out",
            "Waiting");

    assertThat(interaction.getId()).isNotNull();
    assertThat(interaction.getSummary()).isEqualTo("Reached out");
    assertThat(reloadContact(contact).getLastInteractionAt()).isEqualTo(firstOccurredAt);

    Interaction updated =
        interactionService.update(
            user.getId(),
            interaction.getId(),
            InteractionType.PHONE_CALL,
            updatedOccurredAt,
            "Intro call",
            "Good fit");

    assertThat(updated.getInteractionType()).isEqualTo(InteractionType.PHONE_CALL);
    assertThat(updated.getSummary()).isEqualTo("Intro call");
    assertThat(updated.getOutcome()).isEqualTo("Good fit");
    assertThat(reloadContact(contact).getLastInteractionAt()).isEqualTo(updatedOccurredAt);
  }

  @Test
  void createsAndCompletesFollowUpAndRefreshesNextFollowUpAt() {
    User user = createUser();
    Contact contact = createContact(user, "Ada", "Lovelace", null);
    OffsetDateTime dueAt = OffsetDateTime.parse("2026-02-01T09:00:00Z");

    FollowUp followUp = followUpService.create(user.getId(), contact.getId(), null, dueAt, "Reply");

    assertThat(followUp.getId()).isNotNull();
    assertThat(followUp.getStatus()).isEqualTo(FollowUpStatus.OPEN);
    assertThat(reloadContact(contact).getNextFollowUpAt()).isEqualTo(dueAt);

    FollowUp completed = followUpService.complete(user.getId(), followUp.getId());

    assertThat(completed.getStatus()).isEqualTo(FollowUpStatus.COMPLETED);
    assertThat(completed.getCompletedAt()).isNotNull();
    assertThat(reloadContact(contact).getNextFollowUpAt()).isNull();
  }

  @Test
  void enforcesOneOpenFollowUpPerContact() {
    User user = createUser();
    Contact contact = createContact(user, "Ada", "Lovelace", null);
    followUpService.create(
        user.getId(), contact.getId(), null, OffsetDateTime.parse("2026-02-01T09:00:00Z"), null);

    assertThatThrownBy(
            () ->
                followUpService.create(
                    user.getId(),
                    contact.getId(),
                    null,
                    OffsetDateTime.parse("2026-02-02T09:00:00Z"),
                    null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Contact already has an open follow-up.");
  }

  @Test
  void validatesBirthdayFields() {
    User user = createUser();

    assertThatThrownBy(() -> createContactWithBirthday(user, "Ada", (short) 13, (short) 1, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Birthday month must be between 1 and 12.");
    assertThatThrownBy(() -> createContactWithBirthday(user, "Grace", null, (short) 1, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Birthday month and day must be set together.");
    assertThatThrownBy(() -> createContactWithBirthday(user, "Katherine", null, null, 1918))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Birthday year requires month and day.");
    assertThatThrownBy(() -> createContactWithBirthday(user, "Annie", (short) 2, (short) 29, 2025))
        .isInstanceOf(DateTimeException.class);

    Contact leapDayContact = createContactWithBirthday(user, "Mary", (short) 2, (short) 29, 2024);

    assertThat(leapDayContact.getBirthdayMonth()).isEqualTo((short) 2);
    assertThat(leapDayContact.getBirthdayDay()).isEqualTo((short) 29);
    assertThat(leapDayContact.getBirthdayYear()).isEqualTo(2024);
  }

  private User createUser() {
    String id = UUID.randomUUID().toString();
    return userRepository.save(new User("user-" + id + "@example.test", "Test User"));
  }

  private Contact createContact(User user, String firstName) {
    return createContact(user, firstName, null, null);
  }

  private Contact createContact(User user, String firstName, String lastName, String email) {
    return createContactWithBirthday(user, firstName, lastName, email, null, null, null);
  }

  private Contact createContactWithBirthday(
      User user, String firstName, Short birthdayMonth, Short birthdayDay, Integer birthdayYear) {
    return createContactWithBirthday(
        user, firstName, null, null, birthdayMonth, birthdayDay, birthdayYear);
  }

  private Contact createContactWithBirthday(
      User user,
      String firstName,
      String lastName,
      String email,
      Short birthdayMonth,
      Short birthdayDay,
      Integer birthdayYear) {
    return contactService.create(
        user,
        null,
        firstName,
        lastName,
        null,
        null,
        null,
        null,
        email,
        null,
        RelationshipType.PROFESSIONAL,
        null,
        null,
        null,
        birthdayMonth,
        birthdayDay,
        birthdayYear);
  }

  private Contact reloadContact(Contact contact) {
    return contactRepository.findById(contact.getId()).orElseThrow();
  }
}
