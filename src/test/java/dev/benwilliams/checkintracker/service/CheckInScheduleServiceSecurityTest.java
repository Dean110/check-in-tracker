package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.model.CheckInSchedule;
import dev.benwilliams.checkintracker.model.User;
import dev.benwilliams.checkintracker.repository.CheckInScheduleRepository;
import dev.benwilliams.checkintracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CheckInScheduleServiceSecurityTest {
    
    @MockitoBean
    private CheckInScheduleRepository scheduleRepository;
    
    @MockitoBean
    private UserRepository userRepository;
    
    @Autowired
    private CheckInScheduleService checkInScheduleService;
    
    @Test
    @WithMockUser(username = "owner@example.com", roles = "USER")
    void testUserCanAccessOwnSchedules() {
        // Given
        User user = User.builder().email("owner@example.com").build();
        CheckInSchedule schedule = CheckInSchedule.builder().build();
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(scheduleRepository.findByUserAndActiveTrue(user)).thenReturn(Optional.of(schedule));
        
        // When & Then
        assertDoesNotThrow(() -> checkInScheduleService.findByUserEmail("owner@example.com"));
        verify(scheduleRepository).findByUserAndActiveTrue(user);
    }
    
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUserCannotAccessOtherSchedules() {
        // When & Then
        assertThrows(AccessDeniedException.class, 
            () -> checkInScheduleService.findByUserEmail("other@example.com"));
        verify(scheduleRepository, never()).findByUserAndActiveTrue(any());
    }
    
    @Test
    @WithMockUser(username = "owner@example.com", roles = "USER")
    void testUserCanAccessOwnScheduleById() {
        // Given
        User user = User.builder().email("owner@example.com").build();
        CheckInSchedule schedule = CheckInSchedule.builder().id(1L).user(user).build();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        
        // When & Then
        assertDoesNotThrow(() -> checkInScheduleService.findById(1L));
        verify(scheduleRepository, atLeastOnce()).findById(1L);
    }
    
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUserCannotAccessOtherScheduleById() {
        // Given
        User otherUser = User.builder().email("other@example.com").build();
        CheckInSchedule schedule = CheckInSchedule.builder().id(1L).user(otherUser).build();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        
        // When & Then
        assertThrows(AccessDeniedException.class, 
            () -> checkInScheduleService.findById(1L));
    }
    
    @Test
    @WithMockUser(username = "owner@example.com", roles = "USER")
    void testUserCanDeleteOwnSchedule() {
        // Given
        User user = User.builder().email("owner@example.com").build();
        CheckInSchedule schedule = CheckInSchedule.builder().id(1L).user(user).build();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        
        // When & Then
        assertDoesNotThrow(() -> checkInScheduleService.deleteById(1L));
        verify(scheduleRepository).deleteById(1L);
    }
    
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUserCannotDeleteOtherSchedule() {
        // Given
        User otherUser = User.builder().email("other@example.com").build();
        CheckInSchedule schedule = CheckInSchedule.builder().id(1L).user(otherUser).build();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        
        // When & Then
        assertThrows(AccessDeniedException.class, 
            () -> checkInScheduleService.deleteById(1L));
        verify(scheduleRepository, never()).deleteById(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanAccessAllSchedules() {
        // Given
        when(scheduleRepository.findAll()).thenReturn(List.of());
        
        // When & Then
        assertDoesNotThrow(() -> checkInScheduleService.findAll());
        verify(scheduleRepository).findAll();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotAccessAllSchedules() {
        // When & Then
        assertThrows(AccessDeniedException.class, () -> checkInScheduleService.findAll());
        verify(scheduleRepository, never()).findAll();
    }
}
