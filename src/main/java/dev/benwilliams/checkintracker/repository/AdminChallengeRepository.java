package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.Admin;
import dev.benwilliams.checkintracker.model.AdminChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminChallengeRepository extends JpaRepository<AdminChallenge, Long> {
    
    Optional<AdminChallenge> findByChallengeToken(String challengeToken);
    
    List<AdminChallenge> findByAdminAndResolvedFalse(Admin admin);
    
    List<AdminChallenge> findByResolvedFalseAndExpiresAtBefore(LocalDateTime now);
    
    void deleteByExpiresAtBefore(LocalDateTime cutoff);
}
