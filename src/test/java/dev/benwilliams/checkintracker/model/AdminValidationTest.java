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
class AdminValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void testValidAdmin() {
        // Given - Valid admin
        Admin validAdmin = Admin.builder()
                .name("Admin User")
                .email("admin@example.com")
                .build();

        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(validAdmin);

        // Then
        assertAll("Valid admin has no violations",
            () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    void testInvalidName() {
        // Given - Admin with blank name
        Admin adminWithBlankName = Admin.builder()
                .name("")
                .email("admin@example.com")
                .build();

        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(adminWithBlankName);

        // Then
        assertAll("@NotBlank name validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Name is required")
        );
    }

    @Test
    void testInvalidEmail() {
        // Given - Admin with invalid email
        Admin adminWithInvalidEmail = Admin.builder()
                .name("Admin User")
                .email("invalid-email")
                .build();

        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(adminWithInvalidEmail);

        // Then
        assertAll("@Email validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Email should be valid")
        );
    }

    @Test
    void testBlankEmail() {
        // Given - Admin with blank email
        Admin adminWithBlankEmail = Admin.builder()
                .name("Admin User")
                .email("")
                .build();

        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(adminWithBlankEmail);

        // Then - @Email doesn't validate empty strings, only @NotBlank triggers
        assertAll("@NotBlank email validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Email is required")
        );
    }
}
