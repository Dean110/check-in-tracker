package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.repository.AdminChallengeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AdminChallengeServiceSecurityTest {
    
    @MockitoBean
    private AdminChallengeRepository adminChallengeRepository;
    
    @Autowired
    private AdminChallengeService adminChallengeService;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanAccessAllChallenges() {
        // Given
        when(adminChallengeRepository.findAll()).thenReturn(List.of());
        
        // When & Then
        assertDoesNotThrow(() -> adminChallengeService.findAll());
        verify(adminChallengeRepository).findAll();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotAccessAllChallenges() {
        // When & Then
        assertThrows(AccessDeniedException.class, () -> adminChallengeService.findAll());
        verify(adminChallengeRepository, never()).findAll();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanDeleteAllChallenges() {
        // When & Then
        assertDoesNotThrow(() -> adminChallengeService.deleteAllChallenges());
        verify(adminChallengeRepository).deleteAll();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotDeleteAllChallenges() {
        // When & Then
        assertThrows(AccessDeniedException.class, () -> adminChallengeService.deleteAllChallenges());
        verify(adminChallengeRepository, never()).deleteAll();
    }
}
