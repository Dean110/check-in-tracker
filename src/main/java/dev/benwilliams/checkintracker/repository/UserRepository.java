package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByOauthProviderAndOauthSubject(String oauthProvider, String oauthSubject);
    
    boolean existsByEmail(String email);
}
