package dev.benwilliams.checkintracker.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class CheckInRecordValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void testValidCheckInRecord() {
        // Given - Valid check-in record with GPS coordinates
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInRecord validRecord = CheckInRecord.builder()
                .user(user)
                .textLocation("Central Park")
                .latitude(new BigDecimal("40.78509"))
                .longitude(new BigDecimal("-73.96829"))
                .checkInMethod("WEB")
                .build();

        // When
        Set<ConstraintViolation<CheckInRecord>> violations = validator.validate(validRecord);

        // Then
        assertAll("Valid check-in record has no violations",
            () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    void testInvalidLatitude() {
        // Given - Check-in record with invalid latitude
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInRecord recordWithInvalidLatitude = CheckInRecord.builder()
                .user(user)
                .latitude(new BigDecimal("91.0"))  // > 90
                .longitude(new BigDecimal("0.0"))
                .build();

        // When
        Set<ConstraintViolation<CheckInRecord>> violations = validator.validate(recordWithInvalidLatitude);

        // Then
        assertAll("@DecimalMax latitude validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Latitude must be between -90 and 90")
        );
    }

    @Test
    void testInvalidNegativeLatitude() {
        // Given - Check-in record with invalid negative latitude
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInRecord recordWithInvalidLatitude = CheckInRecord.builder()
                .user(user)
                .latitude(new BigDecimal("-91.0"))  // < -90
                .longitude(new BigDecimal("0.0"))
                .build();

        // When
        Set<ConstraintViolation<CheckInRecord>> violations = validator.validate(recordWithInvalidLatitude);

        // Then
        assertAll("@DecimalMin latitude validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Latitude must be between -90 and 90")
        );
    }

    @Test
    void testInvalidLongitude() {
        // Given - Check-in record with invalid longitude
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInRecord recordWithInvalidLongitude = CheckInRecord.builder()
                .user(user)
                .latitude(new BigDecimal("0.0"))
                .longitude(new BigDecimal("181.0"))  // > 180
                .build();

        // When
        Set<ConstraintViolation<CheckInRecord>> violations = validator.validate(recordWithInvalidLongitude);

        // Then
        assertAll("@DecimalMax longitude validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Longitude must be between -180 and 180")
        );
    }

    @Test
    void testInvalidNegativeLongitude() {
        // Given - Check-in record with invalid negative longitude
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInRecord recordWithInvalidLongitude = CheckInRecord.builder()
                .user(user)
                .latitude(new BigDecimal("0.0"))
                .longitude(new BigDecimal("-181.0"))  // < -180
                .build();

        // When
        Set<ConstraintViolation<CheckInRecord>> violations = validator.validate(recordWithInvalidLongitude);

        // Then
        assertAll("@DecimalMin longitude validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Longitude must be between -180 and 180")
        );
    }

    @Test
    void testMultipleInvalidCoordinates() {
        // Given - Check-in record with both invalid coordinates
        User user = User.builder().name("Test").email("test@example.com").build();
        CheckInRecord recordWithInvalidCoordinates = CheckInRecord.builder()
                .user(user)
                .latitude(new BigDecimal("95.0"))   // > 90
                .longitude(new BigDecimal("185.0")) // > 180
                .build();

        // When
        Set<ConstraintViolation<CheckInRecord>> violations = validator.validate(recordWithInvalidCoordinates);

        // Then
        assertAll("Multiple coordinate validation violations",
            () -> assertThat(violations).hasSize(2),
            () -> assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder(
                        "Latitude must be between -90 and 90",
                        "Longitude must be between -180 and 180")
        );
    }
}
