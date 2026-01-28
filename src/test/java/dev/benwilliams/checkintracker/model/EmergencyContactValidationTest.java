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
class EmergencyContactValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void testValidEmergencyContact() {
        // Given - Valid emergency contact
        User user = User.builder().name("Test").email("test@example.com").build();
        EmergencyContact validContact = EmergencyContact.builder()
                .user(user)
                .alias("Mom")
                .email("mom@example.com")
                .phoneNumber("+1234567890")
                .build();

        // When
        Set<ConstraintViolation<EmergencyContact>> violations = validator.validate(validContact);

        // Then
        assertAll("Valid emergency contact has no violations",
            () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    void testBlankAlias() {
        // Given - Emergency contact with blank alias
        User user = User.builder().name("Test").email("test@example.com").build();
        EmergencyContact contactWithBlankAlias = EmergencyContact.builder()
                .user(user)
                .alias("")  // @NotBlank violation
                .email("contact@example.com")
                .build();

        // When
        Set<ConstraintViolation<EmergencyContact>> violations = validator.validate(contactWithBlankAlias);

        // Then
        assertAll("@NotBlank alias validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Alias is required")
        );
    }

    @Test
    void testInvalidEmail() {
        // Given - Emergency contact with invalid email
        User user = User.builder().name("Test").email("test@example.com").build();
        EmergencyContact contactWithInvalidEmail = EmergencyContact.builder()
                .user(user)
                .alias("Contact")
                .email("invalid-email")  // @Email violation
                .build();

        // When
        Set<ConstraintViolation<EmergencyContact>> violations = validator.validate(contactWithInvalidEmail);

        // Then
        assertAll("@Email validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Email should be valid")
        );
    }

    @Test
    void testMultipleInvalidFields() {
        // Given - Emergency contact with multiple invalid fields
        User user = User.builder().name("Test").email("test@example.com").build();
        EmergencyContact contactWithMultipleInvalid = EmergencyContact.builder()
                .user(user)
                .alias("")  // @NotBlank violation
                .email("invalid-email")  // @Email violation
                .build();

        // When
        Set<ConstraintViolation<EmergencyContact>> violations = validator.validate(contactWithMultipleInvalid);

        // Then
        assertAll("Multiple validation violations",
            () -> assertThat(violations).hasSize(2),
            () -> assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("Alias is required", "Email should be valid")
        );
    }
}
