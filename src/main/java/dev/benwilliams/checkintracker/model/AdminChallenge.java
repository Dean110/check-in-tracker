package dev.benwilliams.checkintracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_challenges")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdminChallenge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    @ToString.Exclude
    private Admin admin;
    
    @NotBlank(message = "Challenge token is required")
    @Column(name = "challenge_token", nullable = false, unique = true)
    private String challengeToken;
    
    @NotBlank(message = "Operation is required")
    @Column(nullable = false)
    private String operation; // DELETE_USER, MANAGE_BLOCKS, etc.
    
    @NotNull(message = "Expires at is required")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "resolved")
    @Builder.Default
    private Boolean resolved = false;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
