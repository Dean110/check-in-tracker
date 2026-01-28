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
class UserValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void testValidUser() {
        // Given - Valid user
        User validUser = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        // Then
        assertAll("Valid user has no violations",
            () -> assertThat(violations).isEmpty()
        );
    }

    @Test
    void testInvalidName() {
        // Given - User with blank name
        User userWithBlankName = User.builder()
                .name("")
                .email("john@example.com")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(userWithBlankName);

        // Then
        assertAll("@NotBlank name validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Name is required")
        );
    }

    @Test
    void testInvalidEmail() {
        // Given - User with invalid email
        User userWithInvalidEmail = User.builder()
                .name("John Doe")
                .email("invalid-email")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(userWithInvalidEmail);

        // Then
        assertAll("@Email validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Email should be valid")
        );
    }

    @Test
    void testBlankEmail() {
        // Given - User with blank email
        User userWithBlankEmail = User.builder()
                .name("John Doe")
                .email("")
                .build();

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(userWithBlankEmail);

        // Then - @Email doesn't validate empty strings, only @NotBlank triggers
        assertAll("@NotBlank email validation",
            () -> assertThat(violations).hasSize(1),
            () -> assertThat(violations.iterator().next().getMessage()).isEqualTo("Email is required")
        );
    }
}
