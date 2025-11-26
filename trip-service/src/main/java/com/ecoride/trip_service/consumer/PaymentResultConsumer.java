package com.ecoride.trip_service.consumer;

import com.ecoride.trip_service.event.PaymentResultEvent;
import com.ecoride.trip_service.model.Reservation;
import com.ecoride.trip_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);
    private final ReservationRepository repository;

    @Bean
    public Consumer<PaymentResultEvent> paymentResultProcessor() {
        return event -> {
            log.info("📩 Procesando resultado de pago para Reserva ID: {}", event.getReservationId());

            // Intentamos buscar la reserva
            Reservation reservation = repository.findById(event.getReservationId())
                    .orElse(null);

            // --- CAMBIO AQUÍ: LÓGICA DE REINTENTO ---
            if (reservation == null) {
                // Si no está, lanzamos excepción para que RabbitMQ reencole el mensaje y reintente
                log.warn("⏳ Reserva {} no encontrada aún (posible latencia de transacción). Reintentando...", event.getReservationId());
                throw new RuntimeException("Reserva no encontrada, forzando reintento...");
            }
            // -----------------------------------------

            if ("AUTHORIZED".equals(event.getStatus())) {
                reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
                log.info("✅ Reserva {} confirmada y guardada en BD.", event.getReservationId());
            } else {
                reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
                log.warn("❌ Reserva {} cancelada por fallo de pago.", event.getReservationId());
            }

            repository.save(reservation);
        };
    }
}