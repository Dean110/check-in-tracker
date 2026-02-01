package dev.benwilliams.checkintracker.service;

import dev.benwilliams.checkintracker.model.User;
import dev.benwilliams.checkintracker.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #email")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
}
