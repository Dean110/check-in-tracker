package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.EmergencyContact;
import dev.benwilliams.checkintracker.model.InvitationStatus;
import dev.benwilliams.checkintracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    
    List<EmergencyContact> findByUser(User user);
    
    List<EmergencyContact> findByUserAndOptedInTrue(User user);
    
    List<EmergencyContact> findByUserAndOptedInTrueAndSnoozedUntilBefore(User user, LocalDateTime now);
    
    @Query("SELECT ec FROM EmergencyContact ec WHERE ec.user = :user AND ec.optedIn = true AND (ec.snoozedUntil IS NULL OR ec.snoozedUntil < :now)")
    List<EmergencyContact> findActiveContactsForUser(User user, LocalDateTime now);
    
    List<EmergencyContact> findByInvitationStatus(InvitationStatus status);
}
