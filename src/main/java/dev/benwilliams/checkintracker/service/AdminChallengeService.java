package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.model.Admin;
import dev.benwilliams.checkintracker.model.AdminChallenge;
import dev.benwilliams.checkintracker.repository.AdminChallengeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AdminChallengeService {
    
    private final AdminChallengeRepository adminChallengeRepository;
    
    public AdminChallengeService(AdminChallengeRepository adminChallengeRepository) {
        this.adminChallengeRepository = adminChallengeRepository;
    }
    
    public AdminChallenge createChallenge(Admin admin, String operation) {
        AdminChallenge challenge = AdminChallenge.builder()
                .admin(admin)
                .challengeToken(UUID.randomUUID().toString())
                .operation(operation)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        
        return adminChallengeRepository.save(challenge);
    }
    
    public boolean validateChallenge(String token) {
        return adminChallengeRepository.findByChallengeToken(token)
                .filter(challenge -> !challenge.getResolved())
                .filter(challenge -> challenge.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(challenge -> {
                    challenge.setResolved(true);
                    challenge.setResolvedAt(LocalDateTime.now());
                    adminChallengeRepository.save(challenge);
                    return true;
                })
                .orElse(false);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllChallenges() {
        adminChallengeRepository.deleteAll();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminChallenge> findAll() {
        return adminChallengeRepository.findAll();
    }
}
