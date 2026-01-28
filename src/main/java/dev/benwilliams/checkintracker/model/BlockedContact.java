package dev.benwilliams.checkintracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_contacts")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BlockedContact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @NotBlank(message = "Reason is required")
    @Column(nullable = false)
    private String reason;
    
    @CreationTimestamp
    @Column(name = "blocked_at", updatable = false)
    private LocalDateTime blockedAt;
}
