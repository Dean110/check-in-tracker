package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.MissedCheckInChallenge;
import dev.benwilliams.checkintracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissedCheckInChallengeRepository extends JpaRepository<MissedCheckInChallenge, Long> {
    
    Optional<MissedCheckInChallenge> findByChallengeToken(String challengeToken);
    
    List<MissedCheckInChallenge> findByUserAndResolvedFalse(User user);
    
    List<MissedCheckInChallenge> findByResolvedFalseAndExpiresAtBefore(LocalDateTime now);
    
    void deleteByExpiresAtBefore(LocalDateTime cutoff);
}
