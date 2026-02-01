package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.model.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2Admin implements OAuth2User {
    
    private final OAuth2User oauth2User;
    private final Admin admin;
    
    public CustomOAuth2Admin(OAuth2User oauth2User, Admin admin) {
        this.oauth2User = oauth2User;
        this.admin = admin;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    
    @Override
    public String getName() {
        return admin.getName();
    }
    
    public Admin getAdmin() {
        return admin;
    }
}
