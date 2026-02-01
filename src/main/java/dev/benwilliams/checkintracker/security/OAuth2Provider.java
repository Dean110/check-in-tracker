package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Provider {
    String getProviderName();
    User createUserFromOAuth2User(OAuth2User oauth2User);
    boolean supports(String registrationId);
}
