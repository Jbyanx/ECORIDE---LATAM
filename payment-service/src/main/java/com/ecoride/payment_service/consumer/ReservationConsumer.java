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

            // --- SIMULACIÓN DE LÓGICA DE NEGOCIO ---
            // Si el monto es mayor a 50,000, simulamos que el banco rechaza la tarjeta.
            boolean paymentSuccess;
            if (event.getAmount().compareTo(new java.math.BigDecimal("50000")) > 0) {
                log.warn("⚠️ Monto demasiado alto (${}). Rechazando pago...", event.getAmount());
                paymentSuccess = false;
            } else {
                log.info("✅ Monto aceptable. Aprobando pago...");
                paymentSuccess = true;
            }
            // ---------------------------------------

            String status = paymentSuccess ? "AUTHORIZED" : "FAILED";
            String paymentId = UUID.randomUUID().toString();

            PaymentResultEvent result = new PaymentResultEvent(
                    event.getReservationId(),
                    paymentId,
                    status
            );

            log.info("📤 Enviando resultado de pago: {}", status);
            streamBridge.send("payment-out-0", result);
        };
    }
}