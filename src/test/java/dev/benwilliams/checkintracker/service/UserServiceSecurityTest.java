package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.repository.UserRepository;
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
class UserServiceSecurityTest {
    
    @MockitoBean
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanAccessAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of());
        
        // When & Then
        assertDoesNotThrow(() -> userService.findAll());
        verify(userRepository).findAll();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotAccessAllUsers() {
        // When & Then
        assertThrows(AccessDeniedException.class, () -> userService.findAll());
        verify(userRepository, never()).findAll();
    }
    
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUserCanAccessOwnEmail() {
        // Given
        when(userRepository.findByEmail("user@example.com")).thenReturn(java.util.Optional.empty());
        
        // When & Then
        assertDoesNotThrow(() -> userService.findByEmail("user@example.com"));
        verify(userRepository).findByEmail("user@example.com");
    }
    
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void testUserCannotAccessOtherEmail() {
        // When & Then
        assertThrows(AccessDeniedException.class, () -> userService.findByEmail("other@example.com"));
        verify(userRepository, never()).findByEmail("other@example.com");
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanDeleteUser() {
        // When & Then
        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotDeleteUser() {
        // When & Then
        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(1L);
    }
}
