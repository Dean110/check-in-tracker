package dev.benwilliams.checkintracker.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class BlockedContactValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void testValidBlockedContact() {
        // Given - Valid blocked contact
        BlockedContact validBlockedContact = BlockedContact.builder()
                .email("blocked@example.com")
                .phoneNumber("+1234567890")
                .reason("User requested removal")
                .build();

        // When
        Set<ConstraintViolation<BlockedContact>> violations = validator.validate(validBlockedContact);

        // Then
        assertAll("Valid blocked contact has no violations",
            () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    void testInvalidEmail() {
        // Given - Blocked contact with invalid email
        BlockedContact blockedContactWithInvalidEmail = BlockedContact.builder()
                .email("invalid-email")  // @Email violation
                .reason("User requested removal")
                .build();

        // When
        Set<ConstraintViolation<BlockedContact>> violations = validator.validate(blockedContactWithInvalidEmail);

        // Then
        assertAll("@Email validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Email should be valid")
        );
    }

    @Test
    void testBlankReason() {
        // Given - Blocked contact with blank reason
        BlockedContact blockedContactWithBlankReason = BlockedContact.builder()
                .email("blocked@example.com")
                .reason("")  // @NotBlank violation
                .build();

        // When
        Set<ConstraintViolation<BlockedContact>> violations = validator.validate(blockedContactWithBlankReason);

        // Then
        assertAll("@NotBlank reason validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Reason is required")
        );
    }

    @Test
    void testMultipleInvalidFields() {
        // Given - Blocked contact with multiple invalid fields
        BlockedContact blockedContactWithMultipleInvalid = BlockedContact.builder()
                .email("invalid-email")  // @Email violation
                .reason("")  // @NotBlank violation
                .build();

        // When
        Set<ConstraintViolation<BlockedContact>> violations = validator.validate(blockedContactWithMultipleInvalid);

        // Then
        assertAll("Multiple validation violations",
            () -> assertThat(violations).hasSize(2),
            () -> assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("Email should be valid", "Reason is required")
        );
    }
}
