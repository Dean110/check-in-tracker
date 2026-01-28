package dev.benwilliams.checkintracker.repository;

import dev.benwilliams.checkintracker.model.BlockedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedContactRepository extends JpaRepository<BlockedContact, Long> {
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    Optional<BlockedContact> findByEmail(String email);
    
    Optional<BlockedContact> findByPhoneNumber(String phoneNumber);
}
