package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.model.User;
import dev.benwilliams.checkintracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    private final List<OAuth2Provider> oauth2Providers;
    
    public CustomOAuth2UserService(UserRepository userRepository, List<OAuth2Provider> oauth2Providers) {
        this.userRepository = userRepository;
        this.oauth2Providers = oauth2Providers;
    }
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        OAuth2Provider provider = oauth2Providers.stream()
                .filter(p -> p.supports(registrationId))
                .findFirst()
                .orElseThrow(() -> new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId));
        
        User user = findOrCreateUser(oauth2User, provider);
        
        return new CustomOAuth2User(oauth2User, user);
    }
    
    private User findOrCreateUser(OAuth2User oauth2User, OAuth2Provider provider) {
        String email = oauth2User.getAttribute("email");
        String oauthSubject = oauth2User.getAttribute("sub");
        
        return userRepository.findByOauthProviderAndOauthSubject(provider.getProviderName(), oauthSubject)
                .orElseGet(() -> {
                    User newUser = provider.createUserFromOAuth2User(oauth2User);
                    return userRepository.save(newUser);
                });
    }
}
