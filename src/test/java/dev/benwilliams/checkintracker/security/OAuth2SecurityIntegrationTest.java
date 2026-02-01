package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.model.User;
import dev.benwilliams.checkintracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OAuth2ProvidersIntegrationTest {

    @Autowired
    private GoogleOAuth2Provider googleProvider;

    @Autowired
    private AppleOAuth2Provider appleProvider;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSupportCorrectProviders() {
        assertThat(googleProvider.supports("google")).isTrue();
        assertThat(googleProvider.supports("apple")).isFalse();
        
        assertThat(appleProvider.supports("apple")).isTrue();
        assertThat(appleProvider.supports("google")).isFalse();
    }

    @Test
    void shouldHaveCorrectProviderNames() {
        assertThat(googleProvider.getProviderName()).isEqualTo("google");
        assertThat(appleProvider.getProviderName()).isEqualTo("apple");
    }

    @Test
    void shouldFindExistingUserByOAuthCredentials() {
        // Given
        User user = User.builder()
            .name("Test User")
            .email("test@example.com")
            .oauthProvider("google")
            .oauthSubject("google-123")
            .build();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByOauthProviderAndOauthSubject("google", "google-123");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
}
