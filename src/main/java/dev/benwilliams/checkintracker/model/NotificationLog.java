package dev.benwilliams.checkintracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_contact_id")
    @ToString.Exclude
    private EmergencyContact emergencyContact;
    
    @NotBlank(message = "Notification type is required")
    @Column(name = "notification_type", nullable = false)
    private String notificationType; // EMAIL, SMS
    
    @NotBlank(message = "Message type is required")
    @Column(name = "message_type", nullable = false)
    private String messageType; // INVITATION, MISSED_CHECKIN, CHALLENGE
    
    @NotNull(message = "Sent at is required")
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
    
    @Column(name = "delivery_status")
    private String deliveryStatus; // SENT, DELIVERED, FAILED
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
