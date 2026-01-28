package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.CheckInRecord;
import dev.benwilliams.checkintracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRecordRepository extends JpaRepository<CheckInRecord, Long> {
    
    List<CheckInRecord> findByUserOrderByCheckedInAtDesc(User user);
    
    Optional<CheckInRecord> findTopByUserOrderByCheckedInAtDesc(User user);
    
    List<CheckInRecord> findByUserAndCheckedInAtAfter(User user, LocalDateTime after);
    
    long countByUserAndCheckedInAtAfter(User user, LocalDateTime after);
}
