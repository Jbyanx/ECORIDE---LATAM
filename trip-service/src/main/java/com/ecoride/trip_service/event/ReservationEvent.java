package com.ecoride.trip_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationEvent {
    private Long reservationId;
    private Long tripId;
    private String passengerId;
    private BigDecimal amount;
    private String status; // "PENDING", "CONFIRMED"
}