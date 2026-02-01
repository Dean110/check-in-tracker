package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2ProvidersTest {

    @Test
    void googleProviderShouldCreateUserCorrectly() {
        // Given
        GoogleOAuth2Provider provider = new GoogleOAuth2Provider();
        OAuth2User oauth2User = new DefaultOAuth2User(
            Set.of(),
            Map.of(
                "sub", "google-123",
                "name", "John Doe",
                "email", "john@gmail.com"
            ),
            "sub"
        );

        // When
        User user = provider.createUserFromOAuth2User(oauth2User);

        // Then
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@gmail.com");
        assertThat(user.getOauthProvider()).isEqualTo("google");
        assertThat(user.getOauthSubject()).isEqualTo("google-123");
    }

    @Test
    void appleProviderShouldCreateUserCorrectly() {
        // Given
        AppleOAuth2Provider provider = new AppleOAuth2Provider();
        OAuth2User oauth2User = new DefaultOAuth2User(
            Set.of(),
            Map.of(
                "sub", "apple-456",
                "name", "Jane Smith",
                "email", "jane@icloud.com"
            ),
            "sub"
        );

        // When
        User user = provider.createUserFromOAuth2User(oauth2User);

        // Then
        assertThat(user.getName()).isEqualTo("Jane Smith");
        assertThat(user.getEmail()).isEqualTo("jane@icloud.com");
        assertThat(user.getOauthProvider()).isEqualTo("apple");
        assertThat(user.getOauthSubject()).isEqualTo("apple-456");
    }

    @Test
    void providersShouldSupportCorrectRegistrationIds() {
        GoogleOAuth2Provider googleProvider = new GoogleOAuth2Provider();
        AppleOAuth2Provider appleProvider = new AppleOAuth2Provider();

        assertThat(googleProvider.supports("google")).isTrue();
        assertThat(googleProvider.supports("apple")).isFalse();
        assertThat(googleProvider.supports("facebook")).isFalse();

        assertThat(appleProvider.supports("apple")).isTrue();
        assertThat(appleProvider.supports("google")).isFalse();
        assertThat(appleProvider.supports("facebook")).isFalse();
    }
}
