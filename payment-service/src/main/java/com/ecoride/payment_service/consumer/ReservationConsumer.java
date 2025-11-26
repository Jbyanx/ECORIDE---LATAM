package com.ecoride.payment_service.consumer;

import com.ecoride.payment_service.event.PaymentResultEvent;
import com.ecoride.payment_service.event.ReservationEvent;
import lombok.RequiredArgsConstructor; // Importante para inyectar StreamBridge
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor // Lombok generará el constructor para inyectar streamBridge
public class ReservationConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReservationConsumer.class);
    private final StreamBridge streamBridge; // La herramienta para enviar mensajes

    @Bean
    public Consumer<ReservationEvent> processReservation() {
        return event -> {
            log.info("💳 Procesando pago para Reserva ID: {}", event.getReservationId());

            // 1. Simular lógica de negocio (Aquí se guarda el PaymentIntent en BD)
            boolean paymentSuccess = true; // Simulamos éxito (Happy Path)

            String status = paymentSuccess ? "AUTHORIZED" : "FAILED";
            String paymentId = UUID.randomUUID().toString();

            // 2. Crear evento de respuesta
            PaymentResultEvent result = new PaymentResultEvent(
                    event.getReservationId(),
                    paymentId,
                    status
            );

            // 3. Enviar respuesta a RabbitMQ
            log.info("📤 Enviando resultado de pago: {}", status);
            streamBridge.send("payment-out-0", result);
        };
    }
}