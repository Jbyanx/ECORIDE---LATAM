package com.ecoride.payment_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultEvent {
    private Long reservationId;
    private String paymentId;
    private String status; // "AUTHORIZED", "FAILED"
}