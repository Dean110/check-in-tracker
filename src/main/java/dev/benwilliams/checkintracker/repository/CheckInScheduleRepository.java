package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.CheckInSchedule;
import dev.benwilliams.checkintracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInScheduleRepository extends JpaRepository<CheckInSchedule, Long> {
    
    Optional<CheckInSchedule> findByUserAndActiveTrue(User user);
    
    List<CheckInSchedule> findByActiveTrue();
    
    List<CheckInSchedule> findByActiveTrueAndNextCheckInDueBefore(LocalDateTime now);
}
