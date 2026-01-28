package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    Optional<Admin> findByEmail(String email);
    
    Optional<Admin> findByOauthProviderAndOauthSubject(String oauthProvider, String oauthSubject);
    
    boolean existsByEmail(String email);
}
