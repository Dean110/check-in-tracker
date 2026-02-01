package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.model.CheckInSchedule;
import dev.benwilliams.checkintracker.repository.CheckInScheduleRepository;
import dev.benwilliams.checkintracker.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckInScheduleService {
    
    private final CheckInScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    
    public CheckInScheduleService(CheckInScheduleRepository scheduleRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }
    
    @PreAuthorize("hasRole('USER') and @checkInScheduleService.isOwner(authentication.name, #scheduleId)")
    public Optional<CheckInSchedule> findById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }
    
    @PreAuthorize("hasRole('USER') and @checkInScheduleService.isOwnerByEmail(authentication.name, #userEmail)")
    public List<CheckInSchedule> findByUserEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .flatMap(scheduleRepository::findByUserAndActiveTrue)
                .map(List::of)
                .orElse(List.of());
    }
    
    @PreAuthorize("hasRole('USER')")
    public CheckInSchedule save(CheckInSchedule schedule) {
        return scheduleRepository.save(schedule);
    }
    
    @PreAuthorize("hasRole('USER') and @checkInScheduleService.isOwner(authentication.name, #scheduleId)")
    public void deleteById(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<CheckInSchedule> findAll() {
        return scheduleRepository.findAll();
    }
    
    // Security helper methods
    public boolean isOwner(String authenticatedEmail, Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .map(schedule -> schedule.getUser().getEmail().equals(authenticatedEmail))
                .orElse(false);
    }
    
    public boolean isOwnerByEmail(String authenticatedEmail, String userEmail) {
        return authenticatedEmail.equals(userEmail);
    }
}
