package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.model.Admin;
import dev.benwilliams.checkintracker.model.AdminChallenge;
import dev.benwilliams.checkintracker.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminChallengeServiceIntegrationTest {

    @Autowired
    private AdminChallengeService adminChallengeService;

    @Autowired
    private AdminRepository adminRepository;

    @Test
    void shouldCreateValidChallenge() {
        // Given
        Admin admin = Admin.builder()
            .name("Test Admin")
            .email("admin@example.com")
            .oauthProvider("google")
            .oauthSubject("admin-123")
            .build();
        adminRepository.save(admin);

        // When
        AdminChallenge challenge = adminChallengeService.createChallenge(admin, "DELETE_USER");

        // Then
        assertThat(challenge.getAdmin()).isEqualTo(admin);
        assertThat(challenge.getOperation()).isEqualTo("DELETE_USER");
        assertThat(challenge.getChallengeToken()).isNotNull();
        assertThat(challenge.getExpiresAt()).isAfter(LocalDateTime.now());
        assertThat(challenge.getResolved()).isFalse();
    }

    @Test
    void shouldValidateAndResolveChallenge() {
        // Given
        Admin admin = Admin.builder()
            .name("Test Admin")
            .email("admin@example.com")
            .oauthProvider("google")
            .oauthSubject("admin-456")
            .build();
        adminRepository.save(admin);

        AdminChallenge challenge = adminChallengeService.createChallenge(admin, "MANAGE_BLOCKS");
        String token = challenge.getChallengeToken();

        // When
        boolean isValid = adminChallengeService.validateChallenge(token);

        // Then
        assertThat(isValid).isTrue();
        assertThat(challenge.getResolved()).isTrue();
        assertThat(challenge.getResolvedAt()).isNotNull();
    }

    @Test
    void shouldRejectInvalidToken() {
        // When
        boolean isValid = adminChallengeService.validateChallenge("invalid-token");

        // Then
        assertThat(isValid).isFalse();
    }
}
