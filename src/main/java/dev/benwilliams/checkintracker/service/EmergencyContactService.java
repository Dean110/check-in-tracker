package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.model.EmergencyContact;
import dev.benwilliams.checkintracker.repository.EmergencyContactRepository;
import dev.benwilliams.checkintracker.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmergencyContactService {
    
    private final EmergencyContactRepository emergencyContactRepository;
    private final UserRepository userRepository;
    
    public EmergencyContactService(EmergencyContactRepository emergencyContactRepository, UserRepository userRepository) {
        this.emergencyContactRepository = emergencyContactRepository;
        this.userRepository = userRepository;
    }
    
    @PreAuthorize("hasRole('USER') and @emergencyContactService.isOwner(authentication.name, #contactId)")
    public Optional<EmergencyContact> findById(Long contactId) {
        return emergencyContactRepository.findById(contactId);
    }
    
    @PreAuthorize("hasRole('USER') and @emergencyContactService.isOwnerByEmail(authentication.name, #userEmail)")
    public List<EmergencyContact> findByUserEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .map(emergencyContactRepository::findByUser)
                .orElse(List.of());
    }
    
    @PreAuthorize("hasRole('USER')")
    public EmergencyContact save(EmergencyContact contact) {
        return emergencyContactRepository.save(contact);
    }
    
    @PreAuthorize("hasRole('USER') and @emergencyContactService.isOwner(authentication.name, #contactId)")
    public void deleteById(Long contactId) {
        emergencyContactRepository.deleteById(contactId);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<EmergencyContact> findAll() {
        return emergencyContactRepository.findAll();
    }
    
    // Security helper methods
    public boolean isOwner(String authenticatedEmail, Long contactId) {
        return emergencyContactRepository.findById(contactId)
                .map(contact -> contact.getUser().getEmail().equals(authenticatedEmail))
                .orElse(false);
    }
    
    public boolean isOwnerByEmail(String authenticatedEmail, String userEmail) {
        return authenticatedEmail.equals(userEmail);
    }
}
