package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.EmergencyContact;
import dev.benwilliams.checkintracker.model.NotificationLog;
import dev.benwilliams.checkintracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    
    List<NotificationLog> findByUser(User user);
    
    List<NotificationLog> findByEmergencyContact(EmergencyContact emergencyContact);
    
    List<NotificationLog> findByNotificationType(String notificationType);
    
    List<NotificationLog> findByMessageType(String messageType);
}
