package com.ecoride.notification_service.consumer;

import com.ecoride.notification_service.event.PaymentResultEvent;
import com.ecoride.notification_service.event.ReservationEvent;
import com.ecoride.notification_service.model.Notification;
import com.ecoride.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private final NotificationRepository repository;

    // Escucha: ecoride-reservations
    @Bean
    public Consumer<ReservationEvent> reservationNotification() {
        return event -> {
            log.info("📧 Enviando correo de 'Reserva Recibida' a {}", event.getPassengerId());

            saveNotification(
                    event.getPassengerId(),
                    "Reserva Recibida",
                    "Hola, hemos recibido tu solicitud para el viaje " + event.getTripId() + ". Estamos procesando el pago."
            );
        };
    }

    // Escucha: ecoride-payment-results
    @Bean
    public Consumer<PaymentResultEvent> paymentNotification() {
        return event -> {
            String status = event.getStatus();
            String subject = "AUTHORIZED".equals(status) ? "¡Viaje Confirmado! 🚗" : "Pago Fallido ❌";

            log.info("📧 Enviando correo de '{}' para reserva {}", subject, event.getReservationId());

            saveNotification(
                    "usuario-asociado-reserva-" + event.getReservationId(), // En un caso real, buscaríamos el email en BD
                    subject,
                    "El estado de tu pago es: " + status
            );
        };
    }

    private void saveNotification(String to, String subject, String body) {
        repository.save(Notification.builder()
                .recipient(to)
                .subject(subject)
                .message(body)
                .sentAt(LocalDateTime.now())
                .build());
    }
}