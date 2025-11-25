package com.ecoride.payment_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_intents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId; // El viaje que se quiere pagar

    @Column(name = "passenger_id", nullable = false)
    private String passengerId; // Quién paga

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum PaymentStatus {
        PENDING, AUTHORIZED, FAILED, REFUNDED
    }
}