package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppleOAuth2Provider implements OAuth2Provider {
    
    @Override
    public String getProviderName() {
        return "apple";
    }
    
    @Override
    public User createUserFromOAuth2User(OAuth2User oauth2User) {
        return User.builder()
                .email(oauth2User.getAttribute("email"))
                .name(oauth2User.getAttribute("name"))
                .oauthProvider("apple")
                .oauthSubject(oauth2User.getAttribute("sub"))
                .build();
    }
    
    @Override
    public boolean supports(String registrationId) {
        return "apple".equals(registrationId);
    }
}
