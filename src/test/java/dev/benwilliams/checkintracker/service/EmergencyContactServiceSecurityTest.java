package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.model.EmergencyContact;
import dev.benwilliams.checkintracker.model.User;
import dev.benwilliams.checkintracker.repository.EmergencyContactRepository;
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
class EmergencyContactServiceSecurityTest {
    
    @MockitoBean
    private EmergencyContactRepository emergencyContactRepository;
    
    @MockitoBean
    private UserRepository userRepository;
    
    @Autowired
    private EmergencyContactService emergencyContactService;
    
    @Test
    @WithMockUser(username = "owner@example.com", roles = "USER")
    void testUserCanAccessOwnContacts() {
        // Given
        User user = User.builder().email("owner@example.com").build();
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(emergencyContactRepository.findByUser(user)).thenReturn(List.of());
        
        // When & Then
        assertDoesNotThrow(() -> emergencyContactService.findByUserEmail("owner@example.com"));
        verify(emergencyContactRepository).findByUser(user);
    }
    
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUserCannotAccessOtherContacts() {
        // When & Then
        assertThrows(AccessDeniedException.class, 
            () -> emergencyContactService.findByUserEmail("other@example.com"));
        verify(emergencyContactRepository, never()).findByUser(any());
    }
    
    @Test
    @WithMockUser(username = "owner@example.com", roles = "USER")
    void testUserCanAccessOwnContactById() {
        // Given
        User user = User.builder().email("owner@example.com").build();
        EmergencyContact contact = EmergencyContact.builder().id(1L).user(user).build();
        when(emergencyContactRepository.findById(1L)).thenReturn(Optional.of(contact));
        
        // When & Then
        assertDoesNotThrow(() -> emergencyContactService.findById(1L));
        verify(emergencyContactRepository, atLeastOnce()).findById(1L);
    }
    
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUserCannotAccessOtherContactById() {
        // Given
        User otherUser = User.builder().email("other@example.com").build();
        EmergencyContact contact = EmergencyContact.builder().id(1L).user(otherUser).build();
        when(emergencyContactRepository.findById(1L)).thenReturn(Optional.of(contact));
        
        // When & Then
        assertThrows(AccessDeniedException.class, 
            () -> emergencyContactService.findById(1L));
    }
    
    @Test
    @WithMockUser(username = "owner@example.com", roles = "USER")
    void testUserCanDeleteOwnContact() {
        // Given
        User user = User.builder().email("owner@example.com").build();
        EmergencyContact contact = EmergencyContact.builder().id(1L).user(user).build();
        when(emergencyContactRepository.findById(1L)).thenReturn(Optional.of(contact));
        
        // When & Then
        assertDoesNotThrow(() -> emergencyContactService.deleteById(1L));
        verify(emergencyContactRepository).deleteById(1L);
    }
    
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUserCannotDeleteOtherContact() {
        // Given
        User otherUser = User.builder().email("other@example.com").build();
        EmergencyContact contact = EmergencyContact.builder().id(1L).user(otherUser).build();
        when(emergencyContactRepository.findById(1L)).thenReturn(Optional.of(contact));
        
        // When & Then
        assertThrows(AccessDeniedException.class, 
            () -> emergencyContactService.deleteById(1L));
        verify(emergencyContactRepository, never()).deleteById(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanAccessAllContacts() {
        // Given
        when(emergencyContactRepository.findAll()).thenReturn(List.of());
        
        // When & Then
        assertDoesNotThrow(() -> emergencyContactService.findAll());
        verify(emergencyContactRepository).findAll();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotAccessAllContacts() {
        // When & Then
        assertThrows(AccessDeniedException.class, () -> emergencyContactService.findAll());
        verify(emergencyContactRepository, never()).findAll();
    }
}
