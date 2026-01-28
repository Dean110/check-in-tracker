package dev.benwilliams.checkintracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "check_in_schedules")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CheckInSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;
    
    @NotNull(message = "Interval hours is required")
    @Min(value = 1, message = "Interval must be at least 1 hour")
    @Column(name = "interval_hours", nullable = false)
    private Integer intervalHours;
    
    @NotNull(message = "Grace period minutes is required")
    @Min(value = 0, message = "Grace period cannot be negative")
    @Column(name = "grace_period_minutes", nullable = false)
    private Integer gracePeriodMinutes;
    
    @Column(name = "next_check_in_due")
    private LocalDateTime nextCheckInDue;
    
    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
