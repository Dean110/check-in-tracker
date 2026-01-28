package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.EmergencyContact;
import dev.benwilliams.checkintracker.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class EmergencyContactRepositoryQueryTest {

    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Test
    void testFindActiveContactsForUser_customQuery() {
        // Given
        User user = User.builder()
                .name("Active Query User")
                .email("activequery@example.com")
                .build();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureSnooze = now.plusHours(2);
        LocalDateTime pastSnooze = now.minusHours(1);
        
        // Active contact - opted in, no snooze
        EmergencyContact activeContact1 = EmergencyContact.builder()
                .user(user)
                .alias("Active Contact 1")
                .email("active1@example.com")
                .optedIn(true)
                .snoozedUntil(null)
                .build();
        
        // Active contact - opted in, snooze expired
        EmergencyContact activeContact2 = EmergencyContact.builder()
                .user(user)
                .alias("Active Contact 2")
                .email("active2@example.com")
                .optedIn(true)
                .snoozedUntil(pastSnooze)
                .build();
        
        // Inactive contact - not opted in
        EmergencyContact inactiveContact1 = EmergencyContact.builder()
                .user(user)
                .alias("Inactive Contact 1")
                .email("inactive1@example.com")
                .optedIn(false)
                .build();
        
        // Inactive contact - opted in but snoozed
        EmergencyContact inactiveContact2 = EmergencyContact.builder()
                .user(user)
                .alias("Inactive Contact 2")
                .email("inactive2@example.com")
                .optedIn(true)
                .snoozedUntil(futureSnooze)
                .build();

        // When
        userRepository.save(user);
        emergencyContactRepository.save(activeContact1);
        emergencyContactRepository.save(activeContact2);
        emergencyContactRepository.save(inactiveContact1);
        emergencyContactRepository.save(inactiveContact2);
        entityManager.flush();
        entityManager.clear();

        // Then - Test @Query: findActiveContactsForUser
        List<EmergencyContact> activeContacts = emergencyContactRepository.findActiveContactsForUser(user, now);
        
        assertAll("@Query method - findActiveContactsForUser with mixed scenarios",
            () -> assertThat(activeContacts).hasSize(2),
            () -> assertThat(activeContacts).extracting(EmergencyContact::getAlias)
                    .containsExactlyInAnyOrder("Active Contact 1", "Active Contact 2"),
            () -> assertThat(activeContacts).allMatch(EmergencyContact::getOptedIn),
            () -> assertThat(activeContacts).allMatch(contact -> 
                    contact.getSnoozedUntil() == null || contact.getSnoozedUntil().isBefore(now))
        );
    }

    @Test
    void testFindActiveContactsForUser_nullSnoozedUntil() {
        // Given - Test NULL handling in @Query
        User user = User.builder()
                .name("Null Snooze User")
                .email("nullsnooze@example.com")
                .build();
        
        LocalDateTime now = LocalDateTime.now();
        
        EmergencyContact contactWithNullSnooze = EmergencyContact.builder()
                .user(user)
                .alias("Null Snooze Contact")
                .email("nullsnooze@example.com")
                .optedIn(true)
                .snoozedUntil(null)
                .build();

        // When
        userRepository.save(user);
        emergencyContactRepository.save(contactWithNullSnooze);
        entityManager.flush();
        entityManager.clear();

        // Then - Test @Query handles NULL snoozedUntil correctly
        List<EmergencyContact> activeContacts = emergencyContactRepository.findActiveContactsForUser(user, now);
        
        assertAll("@Query method - NULL snoozedUntil handling",
            () -> assertThat(activeContacts).hasSize(1),
            () -> assertThat(activeContacts.get(0).getAlias()).isEqualTo("Null Snooze Contact"),
            () -> assertThat(activeContacts.get(0).getSnoozedUntil()).isNull(),
            () -> assertThat(activeContacts.get(0).getOptedIn()).isTrue()
        );
    }

    @Test
    void testFindActiveContactsForUser_emptyResult() {
        // Given - All contacts are inactive
        User user = User.builder()
                .name("No Active User")
                .email("noactive@example.com")
                .build();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureSnooze = now.plusHours(1);
        
        EmergencyContact snoozedContact = EmergencyContact.builder()
                .user(user)
                .alias("Snoozed Contact")
                .email("snoozed@example.com")
                .optedIn(true)
                .snoozedUntil(futureSnooze)
                .build();
        
        EmergencyContact notOptedInContact = EmergencyContact.builder()
                .user(user)
                .alias("Not Opted In")
                .email("notoptedin@example.com")
                .optedIn(false)
                .build();

        // When
        userRepository.save(user);
        emergencyContactRepository.save(snoozedContact);
        emergencyContactRepository.save(notOptedInContact);
        entityManager.flush();
        entityManager.clear();

        // Then - Test @Query returns empty list when no active contacts
        List<EmergencyContact> activeContacts = emergencyContactRepository.findActiveContactsForUser(user, now);
        
        assertAll("@Query method - no active contacts scenario",
            () -> assertThat(activeContacts).isEmpty(),
            () -> assertThat(activeContacts).hasSize(0)
        );
    }

    @Test
    void testFindActiveContactsForUser_exactTimeBoundary() {
        // Given - Test exact time boundary (snoozedUntil = now)
        User user = User.builder()
                .name("Boundary Test User")
                .email("boundary@example.com")
                .build();
        
        LocalDateTime now = LocalDateTime.now();
        
        EmergencyContact exactTimeContact = EmergencyContact.builder()
                .user(user)
                .alias("Exact Time Contact")
                .email("exacttime@example.com")
                .optedIn(true)
                .snoozedUntil(now) // Exactly equal to now
                .build();

        // When
        userRepository.save(user);
        emergencyContactRepository.save(exactTimeContact);
        entityManager.flush();
        entityManager.clear();

        // Then - Test @Query with exact time boundary (should NOT be active since snoozedUntil = now, not < now)
        List<EmergencyContact> activeContacts = emergencyContactRepository.findActiveContactsForUser(user, now);
        
        assertAll("@Query method - exact time boundary (snoozedUntil = now)",
            () -> assertThat(activeContacts).isEmpty(),
            () -> assertThat(activeContacts).hasSize(0)
        );
    }

    @Test
    void testFindActiveContactsForUser_userIsolation() {
        // Given - Test that query only returns contacts for specified user
        User user1 = User.builder()
                .name("User One")
                .email("user1@example.com")
                .build();
        
        User user2 = User.builder()
                .name("User Two")
                .email("user2@example.com")
                .build();
        
        LocalDateTime now = LocalDateTime.now();
        
        EmergencyContact user1Contact = EmergencyContact.builder()
                .user(user1)
                .alias("User 1 Contact")
                .email("user1contact@example.com")
                .optedIn(true)
                .snoozedUntil(null)
                .build();
        
        EmergencyContact user2Contact = EmergencyContact.builder()
                .user(user2)
                .alias("User 2 Contact")
                .email("user2contact@example.com")
                .optedIn(true)
                .snoozedUntil(null)
                .build();

        // When
        userRepository.save(user1);
        userRepository.save(user2);
        emergencyContactRepository.save(user1Contact);
        emergencyContactRepository.save(user2Contact);
        entityManager.flush();
        entityManager.clear();

        // Then - Test @Query returns only contacts for specified user
        List<EmergencyContact> user1ActiveContacts = emergencyContactRepository.findActiveContactsForUser(user1, now);
        List<EmergencyContact> user2ActiveContacts = emergencyContactRepository.findActiveContactsForUser(user2, now);
        
        assertAll("@Query method - user isolation",
            () -> assertThat(user1ActiveContacts).hasSize(1),
            () -> assertThat(user1ActiveContacts.get(0).getAlias()).isEqualTo("User 1 Contact"),
            () -> assertThat(user1ActiveContacts.get(0).getUser().getName()).isEqualTo("User One"),
            () -> assertThat(user2ActiveContacts).hasSize(1),
            () -> assertThat(user2ActiveContacts.get(0).getAlias()).isEqualTo("User 2 Contact"),
            () -> assertThat(user2ActiveContacts.get(0).getUser().getName()).isEqualTo("User Two")
        );
    }

    @Test
    void testFindActiveContactsForUser_microsecondPrecision() {
        // Given - Test very close time precision
        User user = User.builder()
                .name("Precision User")
                .email("precision@example.com")
                .build();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime justBefore = now.minusNanos(1000); // 1 microsecond before
        LocalDateTime justAfter = now.plusNanos(1000);   // 1 microsecond after
        
        EmergencyContact justBeforeContact = EmergencyContact.builder()
                .user(user)
                .alias("Just Before Contact")
                .email("justbefore@example.com")
                .optedIn(true)
                .snoozedUntil(justBefore)
                .build();
        
        EmergencyContact justAfterContact = EmergencyContact.builder()
                .user(user)
                .alias("Just After Contact")
                .email("justafter@example.com")
                .optedIn(true)
                .snoozedUntil(justAfter)
                .build();

        // When
        userRepository.save(user);
        emergencyContactRepository.save(justBeforeContact);
        emergencyContactRepository.save(justAfterContact);
        entityManager.flush();
        entityManager.clear();

        // Then - Test @Query with microsecond precision
        List<EmergencyContact> activeContacts = emergencyContactRepository.findActiveContactsForUser(user, now);
        
        assertAll("@Query method - microsecond precision",
            () -> assertThat(activeContacts).hasSize(1),
            () -> assertThat(activeContacts.get(0).getAlias()).isEqualTo("Just Before Contact"),
            () -> assertThat(activeContacts.get(0).getSnoozedUntil()).isBefore(now)
        );
    }
}
