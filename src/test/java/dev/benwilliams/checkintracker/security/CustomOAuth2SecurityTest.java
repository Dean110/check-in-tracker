package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.model.Admin;
import dev.benwilliams.checkintracker.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomOAuth2SecurityTest {

    @Test
    void customOAuth2UserShouldHaveUserRole() {
        // Given
        OAuth2User oauth2User = new DefaultOAuth2User(
            Set.of(),
            Map.of("sub", "123", "name", "Test User", "email", "test@example.com"),
            "sub"
        );
        
        User user = User.builder()
            .name("Test User")
            .email("test@example.com")
            .build();

        // When
        CustomOAuth2User customUser = new CustomOAuth2User(oauth2User, user);

        // Then
        assertThat(customUser.getName()).isEqualTo("Test User");
        assertThat(customUser.getUser()).isEqualTo(user);
        assertThat(customUser.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_USER");
    }

    @Test
    void customOAuth2AdminShouldHaveAdminRole() {
        // Given
        OAuth2User oauth2User = new DefaultOAuth2User(
            Set.of(),
            Map.of("sub", "456", "name", "Test Admin", "email", "admin@example.com"),
            "sub"
        );
        
        Admin admin = Admin.builder()
            .name("Test Admin")
            .email("admin@example.com")
            .build();

        // When
        CustomOAuth2Admin customAdmin = new CustomOAuth2Admin(oauth2User, admin);

        // Then
        assertThat(customAdmin.getName()).isEqualTo("Test Admin");
        assertThat(customAdmin.getAdmin()).isEqualTo(admin);
        assertThat(customAdmin.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_ADMIN");
    }

    @Test
    void customOAuth2UserShouldPreserveOAuth2Attributes() {
        // Given
        Map<String, Object> attributes = Map.of(
            "sub", "123",
            "name", "Test User",
            "email", "test@example.com",
            "picture", "https://example.com/avatar.jpg"
        );
        
        OAuth2User oauth2User = new DefaultOAuth2User(Set.of(), attributes, "sub");
        User user = User.builder().name("Test User").email("test@example.com").build();

        // When
        CustomOAuth2User customUser = new CustomOAuth2User(oauth2User, user);

        // Then
        assertThat(customUser.getAttributes()).isEqualTo(attributes);
        assertThat((Object) customUser.getAttribute("picture")).isEqualTo("https://example.com/avatar.jpg");
    }
}
