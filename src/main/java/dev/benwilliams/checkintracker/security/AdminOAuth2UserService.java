package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.model.Admin;
import dev.benwilliams.checkintracker.repository.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AdminOAuth2UserService extends DefaultOAuth2UserService {
    
    private final AdminRepository adminRepository;
    private final List<OAuth2Provider> oauth2Providers;
    
    public AdminOAuth2UserService(AdminRepository adminRepository, List<OAuth2Provider> oauth2Providers) {
        this.adminRepository = adminRepository;
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
        
        Admin admin = findOrCreateAdmin(oauth2User, provider);
        
        return new CustomOAuth2Admin(oauth2User, admin);
    }
    
    private Admin findOrCreateAdmin(OAuth2User oauth2User, OAuth2Provider provider) {
        String oauthSubject = oauth2User.getAttribute("sub");
        
        return adminRepository.findByOauthProviderAndOauthSubject(provider.getProviderName(), oauthSubject)
                .orElseThrow(() -> new OAuth2AuthenticationException("Admin not found - contact system administrator"));
    }
}
