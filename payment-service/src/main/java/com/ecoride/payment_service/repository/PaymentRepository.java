package com.ecoride.payment_service.repository;

import com.ecoride.payment_service.model.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentIntent, Long> {
}