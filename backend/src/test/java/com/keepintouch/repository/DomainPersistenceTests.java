package com.keepintouch.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.keepintouch.domain.Company;
import com.keepintouch.domain.Contact;
import com.keepintouch.domain.ContactStatus;
import com.keepintouch.domain.FollowUp;
import com.keepintouch.domain.FollowUpStatus;
import com.keepintouch.domain.Interaction;
import com.keepintouch.domain.InteractionType;
import com.keepintouch.domain.RelationshipType;
import com.keepintouch.domain.User;
import jakarta.persistence.EntityManager;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
      "spring.config.import=optional:file:../.env[.properties]",
      "app.local-user-email=local@keep-in-touch.test"
    })
@Transactional
class DomainPersistenceTests {

  private final UserRepository userRepository;

  private final CompanyRepository companyRepository;

  private final ContactRepository contactRepository;

  private final InteractionRepository interactionRepository;

  private final FollowUpRepository followUpRepository;

  private final EntityManager entityManager;

  @Autowired
  DomainPersistenceTests(
      UserRepository userRepository,
      CompanyRepository companyRepository,
      ContactRepository contactRepository,
      InteractionRepository interactionRepository,
      FollowUpRepository followUpRepository,
      EntityManager entityManager) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.contactRepository = contactRepository;
    this.interactionRepository = interactionRepository;
    this.followUpRepository = followUpRepository;
    this.entityManager = entityManager;
  }

  @Test
  void savesAndQueriesCoreDomainGraph() {
    User user =
        userRepository.save(
            new User("phase4-" + UUID.randomUUID() + "@keep-in-touch.test", "Phase 4"));
    Company company = companyRepository.save(new Company(user, "Example Company"));

    Contact contact = new Contact(user, "Ada", RelationshipType.PROFESSIONAL);
    contact.setCompany(company);
    contact.setEmail("ada-" + UUID.randomUUID() + "@example.com");
    contact = contactRepository.save(contact);

    Interaction interaction =
        new Interaction(
            contact,
            InteractionType.EMAIL,
            OffsetDateTime.now().minusDays(1),
            "Exchanged project notes.");
    interaction = interactionRepository.save(interaction);

    FollowUp followUp = new FollowUp(contact, OffsetDateTime.now().plusDays(7));
    followUp.setInteraction(interaction);
    followUp.setReason("Send a follow-up note.");
    followUp = followUpRepository.save(followUp);

    entityManager.flush();
    entityManager.clear();

    assertThat(companyRepository.findByUserIdOrderByNameAsc(user.getId()))
        .extracting(Company::getName)
        .containsExactly("Example Company");

    Contact savedContact = contactRepository.findById(contact.getId()).orElseThrow();
    assertThat(savedContact.getStatus()).isEqualTo(ContactStatus.NEW);
    assertThat(savedContact.getCompany().getName()).isEqualTo("Example Company");
    assertThat(savedContact.getCreatedAt()).isNotNull();
    assertThat(savedContact.getUpdatedAt()).isNotNull();

    assertThat(interactionRepository.findByContactIdOrderByOccurredAtDesc(contact.getId()))
        .extracting(Interaction::getSummary)
        .containsExactly("Exchanged project notes.");

    FollowUp savedFollowUp =
        followUpRepository
            .findByContactIdAndStatus(contact.getId(), FollowUpStatus.OPEN)
            .orElseThrow();
    assertThat(savedFollowUp.getId()).isEqualTo(followUp.getId());
    assertThat(savedFollowUp.getInteraction().getId()).isEqualTo(interaction.getId());
    assertThat(savedFollowUp.getCreatedAt()).isNotNull();
    assertThat(savedFollowUp.getUpdatedAt()).isNotNull();
  }
}
