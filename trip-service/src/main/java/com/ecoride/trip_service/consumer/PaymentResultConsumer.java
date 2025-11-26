package com.ecoride.trip_service.consumer;

import com.ecoride.trip_service.event.PaymentResultEvent;
import com.ecoride.trip_service.model.Reservation;
import com.ecoride.trip_service.model.Trip;
import com.ecoride.trip_service.repository.ReservationRepository;
import com.ecoride.trip_service.repository.TripRepository; // <--- IMPORTANTE
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional; // <--- Para atomicidad

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);
    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository; // <--- Inyectamos esto para guardar el viaje

    @Bean
    public Consumer<PaymentResultEvent> paymentResultProcessor() {
        return event -> {
            processTransaction(event); // Delegamos a un método transaccional
        };
    }

    // Creamos un métod o separado y Transaccional para asegurar que
    // la actualización de la Reserva y del Viaje ocurran juntas o fallen juntas.
    @Transactional
    public void processTransaction(PaymentResultEvent event) {
        log.info("📩 Procesando resultado de pago para Reserva ID: {}", event.getReservationId());

        Reservation reservation = reservationRepository.findById(event.getReservationId())
                .orElse(null);

        if (reservation == null) {
            log.warn("⏳ Reserva {} no encontrada aún. Reintentando...", event.getReservationId());
            throw new RuntimeException("Reserva no encontrada, forzando reintento...");
        }

        // Evitar procesar eventos duplicados si ya estaba confirmada/cancelada
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            log.info("⚠️ La reserva {} ya fue procesada anteriormente. Estado actual: {}",
                    event.getReservationId(), reservation.getStatus());
            return;
        }

        if ("AUTHORIZED".equals(event.getStatus())) {
            // 1. Confirmar Reserva
            reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);

            // 2. ACTUALIZAR ASIENTOS DEL VIAJE
            Trip trip = reservation.getTrip();
            if (trip.getSeatsAvailable() > 0) {
                trip.setSeatsAvailable(trip.getSeatsAvailable() - 1);
                tripRepository.save(trip); // Guardamos el viaje actualizado
                log.info("✅ Reserva {} confirmada. Asientos restantes: {}", event.getReservationId(), trip.getSeatsAvailable());
            } else {
                // Caso borde: Se acabaron los asientos mientras pagaba (Race Condition extrema)
                log.error("❌ PAGO APROBADO PERO SIN ASIENTOS. Se requiere reembolso manual para Reserva {}", event.getReservationId());
                // En un sistema real, aquí iniciaríamos una nueva Saga de "Reembolso"
                reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
            }

        } else {
            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
            log.warn("❌ Reserva {} cancelada por fallo de pago.", event.getReservationId());
        }

        reservationRepository.save(reservation);
    }
}