package dev.benwilliams.checkintracker.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class CheckInScheduleValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void testValidCheckInSchedule() {
        // Given - Valid check-in schedule
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInSchedule validSchedule = CheckInSchedule.builder()
                .user(user)
                .intervalHours(24)
                .gracePeriodMinutes(60)
                .nextCheckInDue(LocalDateTime.now().plusHours(24))
                .build();

        // When
        Set<ConstraintViolation<CheckInSchedule>> violations = validator.validate(validSchedule);

        // Then
        assertAll("Valid check-in schedule has no violations",
            () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    void testInvalidIntervalHours() {
        // Given - Schedule with invalid interval hours
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInSchedule scheduleWithInvalidInterval = CheckInSchedule.builder()
                .user(user)
                .intervalHours(0)  // < 1
                .gracePeriodMinutes(60)
                .build();

        // When
        Set<ConstraintViolation<CheckInSchedule>> violations = validator.validate(scheduleWithInvalidInterval);

        // Then
        assertAll("@Min interval hours validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Interval must be at least 1 hour")
        );
    }

    @Test
    void testNegativeGracePeriod() {
        // Given - Schedule with negative grace period
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInSchedule scheduleWithNegativeGrace = CheckInSchedule.builder()
                .user(user)
                .intervalHours(24)
                .gracePeriodMinutes(-1)  // < 0
                .build();

        // When
        Set<ConstraintViolation<CheckInSchedule>> violations = validator.validate(scheduleWithNegativeGrace);

        // Then
        assertAll("@Min grace period validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Grace period cannot be negative")
        );
    }

    @Test
    void testMultipleInvalidValues() {
        // Given - Schedule with multiple invalid values
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInSchedule scheduleWithMultipleInvalid = CheckInSchedule.builder()
                .user(user)
                .intervalHours(0)   // < 1
                .gracePeriodMinutes(-5)  // < 0
                .build();

        // When
        Set<ConstraintViolation<CheckInSchedule>> violations = validator.validate(scheduleWithMultipleInvalid);

        // Then
        assertAll("Multiple validation violations",
            () -> assertThat(violations).hasSize(2),
            () -> assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder(
                        "Interval must be at least 1 hour",
                        "Grace period cannot be negative")
        );
    }
}
