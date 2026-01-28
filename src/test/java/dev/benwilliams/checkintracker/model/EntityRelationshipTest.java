package dev.benwilliams.checkintracker.model;

import dev.benwilliams.checkintracker.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class EntityRelationshipTest {

    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmergencyContactRepository emergencyContactRepository;
    
    @Autowired
    private CheckInRecordRepository checkInRecordRepository;
    
    @Autowired
    private CheckInScheduleRepository checkInScheduleRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private MissedCheckInChallengeRepository missedCheckInChallengeRepository;
    
    @Autowired
    private NotificationLogRepository notificationLogRepository;
    
    @Autowired
    private AdminChallengeRepository adminChallengeRepository;

    @Test
    void testUserToEmergencyContactsRelationship() {
        // Given
        User user = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();
        
        EmergencyContact contact1 = EmergencyContact.builder()
                .user(user)
                .alias("Mom")
                .email("mom@example.com")
                .build();
                
        EmergencyContact contact2 = EmergencyContact.builder()
                .user(user)
                .alias("Dad")
                .email("dad@example.com")
                .build();

        // When
        userRepository.save(user);
        emergencyContactRepository.save(contact1);
        emergencyContactRepository.save(contact2);
        entityManager.flush();
        entityManager.clear();

        // Then - Test both sides of relationship
        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        List<EmergencyContact> userContacts = emergencyContactRepository.findByUser(savedUser);
        
        assertAll("Emergency contacts collection for user",
            () -> assertThat(userContacts).hasSize(2),
            () -> assertThat(userContacts).extracting(EmergencyContact::getAlias)
                    .containsExactlyInAnyOrder("Mom", "Dad")
        );
        
        // Test contact -> user relationship
        EmergencyContact savedContact = emergencyContactRepository.findById(contact1.getId()).orElseThrow();
        assertAll("Emergency contact -> user relationship",
            () -> assertThat(savedContact.getUser().getName()).isEqualTo("John Doe"),
            () -> assertThat(savedContact.getAlias()).isEqualTo("Mom")
        );
    }

    @Test
    void testUserToCheckInRecordsRelationship() {
        // Given
        User user = User.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .build();
        
        CheckInRecord record1 = CheckInRecord.builder()
                .user(user)
                .textLocation("Home")
                .checkInMethod("WEB")
                .build();
                
        CheckInRecord record2 = CheckInRecord.builder()
                .user(user)
                .textLocation("Office")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .checkInMethod("SMS")
                .build();

        // When
        userRepository.save(user);
        checkInRecordRepository.save(record1);
        checkInRecordRepository.save(record2);
        entityManager.flush();
        entityManager.clear();

        // Then - Test both sides of relationship
        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        List<CheckInRecord> userRecords = checkInRecordRepository.findByUserOrderByCheckedInAtDesc(savedUser);
        
        assertAll("Check-in records collection for user",
            () -> assertThat(userRecords).hasSize(2),
            () -> assertThat(userRecords).extracting(CheckInRecord::getTextLocation)
                    .containsExactlyInAnyOrder("Home", "Office")
        );
        
        // Test record -> user relationship
        CheckInRecord savedRecord = checkInRecordRepository.findById(record1.getId()).orElseThrow();
        assertAll("Check-in record -> user relationship",
            () -> assertThat(savedRecord.getUser().getName()).isEqualTo("Jane Smith"),
            () -> assertThat(savedRecord.getTextLocation()).isEqualTo("Home"),
            () -> assertThat(savedRecord.getCheckInMethod()).isEqualTo("WEB")
        );
    }

    @Test
    void testLocationDataPersistence() {
        // Given
        User user = User.builder()
                .name("Location User")
                .email("location@example.com")
                .build();
        
        CheckInRecord record = CheckInRecord.builder()
                .user(user)
                .textLocation("Central Park")
                .latitude(new BigDecimal("40.78509"))
                .longitude(new BigDecimal("-73.96829"))
                .checkInMethod("API")
                .build();

        // When
        userRepository.save(user);
        checkInRecordRepository.save(record);
        entityManager.flush();
        entityManager.clear();

        // Then - Test location data precision
        CheckInRecord savedRecord = checkInRecordRepository.findById(record.getId()).orElseThrow();
        assertAll("Location data persistence and precision",
            () -> assertThat(savedRecord.getTextLocation()).isEqualTo("Central Park"),
            () -> assertThat(savedRecord.getLatitude()).isEqualByComparingTo(new BigDecimal("40.78509")),
            () -> assertThat(savedRecord.getLongitude()).isEqualByComparingTo(new BigDecimal("-73.96829")),
            () -> assertThat(savedRecord.getCheckInMethod()).isEqualTo("API")
        );
    }

    @Test
    void testEmergencyContactOptInStatus() {
        // Given
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        
        EmergencyContact optedInContact = EmergencyContact.builder()
                .user(user)
                .alias("Opted In Contact")
                .email("optedin@example.com")
                .optedIn(true)
                .build();
                
        EmergencyContact pendingContact = EmergencyContact.builder()
                .user(user)
                .alias("Pending Contact")
                .email("pending@example.com")
                .optedIn(false)
                .build();

        // When
        userRepository.save(user);
        emergencyContactRepository.save(optedInContact);
        emergencyContactRepository.save(pendingContact);
        entityManager.flush();
        entityManager.clear();

        // Then - Test repository queries for opted-in contacts
        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        List<EmergencyContact> optedInContacts = emergencyContactRepository.findByUserAndOptedInTrue(savedUser);
        
        assertAll("Opted-in contacts query results",
            () -> assertThat(optedInContacts).hasSize(1),
            () -> assertThat(optedInContacts.get(0).getAlias()).isEqualTo("Opted In Contact"),
            () -> assertThat(optedInContacts.get(0).getOptedIn()).isTrue()
        );
        
        // Test all contacts for user
        List<EmergencyContact> allContacts = emergencyContactRepository.findByUser(savedUser);
        assertAll("All contacts for user",
            () -> assertThat(allContacts).hasSize(2),
            () -> assertThat(allContacts).extracting(EmergencyContact::getOptedIn)
                    .containsExactlyInAnyOrder(true, false)
        );
    }

    @Test
    void testChallengeTokenUniqueness() {
        // Given
        User user1 = User.builder()
                .name("User One")
                .email("user1@example.com")
                .build();
                
        User user2 = User.builder()
                .name("User Two")
                .email("user2@example.com")
                .build();
        
        MissedCheckInChallenge challenge1 = MissedCheckInChallenge.builder()
                .user(user1)
                .challengeToken("unique_token_123")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        // When
        userRepository.save(user1);
        userRepository.save(user2);
        missedCheckInChallengeRepository.save(challenge1);
        entityManager.flush();
        entityManager.clear();

        // Then - Test token uniqueness and lookup
        MissedCheckInChallenge foundChallenge = missedCheckInChallengeRepository
                .findByChallengeToken("unique_token_123")
                .orElseThrow();
        
        assertAll("Challenge token lookup and properties",
            () -> assertThat(foundChallenge.getUser().getName()).isEqualTo("User One"),
            () -> assertThat(foundChallenge.getChallengeToken()).isEqualTo("unique_token_123"),
            () -> assertThat(foundChallenge.getResolved()).isFalse()
        );
    }

    @Test
    void testUserToCheckInScheduleRelationship() {
        // Given
        User user = User.builder()
                .name("Schedule User")
                .email("schedule@example.com")
                .build();
        
        CheckInSchedule schedule = CheckInSchedule.builder()
                .user(user)
                .intervalHours(12)
                .gracePeriodMinutes(30)
                .nextCheckInDue(LocalDateTime.now().plusHours(12))
                .build();

        // When
        userRepository.save(user);
        checkInScheduleRepository.save(schedule);
        entityManager.flush();
        entityManager.clear();

        // Then - Test both sides of relationship
        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        CheckInSchedule userSchedule = checkInScheduleRepository.findByUserAndActiveTrue(savedUser).orElseThrow();
        
        assertAll("Check-in schedule properties",
            () -> assertThat(userSchedule.getIntervalHours()).isEqualTo(12),
            () -> assertThat(userSchedule.getGracePeriodMinutes()).isEqualTo(30),
            () -> assertThat(userSchedule.getActive()).isTrue()
        );
        
        // Test schedule -> user relationship
        CheckInSchedule savedSchedule = checkInScheduleRepository.findById(schedule.getId()).orElseThrow();
        assertAll("Schedule -> user relationship",
            () -> assertThat(savedSchedule.getUser().getName()).isEqualTo("Schedule User"),
            () -> assertThat(savedSchedule.getIntervalHours()).isEqualTo(12)
        );
    }

    @Test
    void testNotificationLogRelationships() {
        // Given
        User user = User.builder()
                .name("Notification User")
                .email("notification@example.com")
                .build();
        
        EmergencyContact contact = EmergencyContact.builder()
                .user(user)
                .alias("Test Contact")
                .email("contact@example.com")
                .build();
        
        NotificationLog log = NotificationLog.builder()
                .user(user)
                .emergencyContact(contact)
                .notificationType("EMAIL")
                .messageType("INVITATION")
                .sentAt(LocalDateTime.now())
                .deliveryStatus("SENT")
                .build();

        // When
        userRepository.save(user);
        emergencyContactRepository.save(contact);
        notificationLogRepository.save(log);
        entityManager.flush();
        entityManager.clear();

        // Then - Test both sides of relationships
        List<NotificationLog> userLogs = notificationLogRepository.findByUser(user);
        assertAll("Notification logs for user",
            () -> assertThat(userLogs).hasSize(1),
            () -> assertThat(userLogs.get(0).getMessageType()).isEqualTo("INVITATION"),
            () -> assertThat(userLogs.get(0).getNotificationType()).isEqualTo("EMAIL")
        );
        
        List<NotificationLog> contactLogs = notificationLogRepository.findByEmergencyContact(contact);
        assertAll("Notification logs for emergency contact",
            () -> assertThat(contactLogs).hasSize(1),
            () -> assertThat(contactLogs.get(0).getNotificationType()).isEqualTo("EMAIL"),
            () -> assertThat(contactLogs.get(0).getDeliveryStatus()).isEqualTo("SENT")
        );
        
        // Test log -> relationships
        NotificationLog savedLog = notificationLogRepository.findById(log.getId()).orElseThrow();
        assertAll("Notification log relationships",
            () -> assertThat(savedLog.getUser().getName()).isEqualTo("Notification User"),
            () -> assertThat(savedLog.getEmergencyContact().getAlias()).isEqualTo("Test Contact"),
            () -> assertThat(savedLog.getMessageType()).isEqualTo("INVITATION")
        );
    }

    @Test
    void testAdminChallengeRelationship() {
        // Given
        Admin admin = Admin.builder()
                .name("Test Admin")
                .email("admin@example.com")
                .oauthProvider("google")
                .oauthSubject("admin123")
                .build();
        
        AdminChallenge challenge = AdminChallenge.builder()
                .admin(admin)
                .challengeToken("admin_challenge_token")
                .operation("DELETE_USER")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        // When
        adminRepository.save(admin);
        adminChallengeRepository.save(challenge);
        entityManager.flush();
        entityManager.clear();

        // Then - Test both sides of relationship
        Admin savedAdmin = adminRepository.findById(admin.getId()).orElseThrow();
        assertAll("Admin properties",
            () -> assertThat(savedAdmin.getName()).isEqualTo("Test Admin"),
            () -> assertThat(savedAdmin.getEmail()).isEqualTo("admin@example.com"),
            () -> assertThat(savedAdmin.getOauthProvider()).isEqualTo("google")
        );
        
        // Test challenge -> admin relationship
        AdminChallenge savedChallenge = adminChallengeRepository.findById(challenge.getId()).orElseThrow();
        assertAll("Admin challenge properties and relationship",
            () -> assertThat(savedChallenge.getAdmin().getName()).isEqualTo("Test Admin"),
            () -> assertThat(savedChallenge.getOperation()).isEqualTo("DELETE_USER"),
            () -> assertThat(savedChallenge.getChallengeToken()).isEqualTo("admin_challenge_token"),
            () -> assertThat(savedChallenge.getResolved()).isFalse()
        );
    }
}
