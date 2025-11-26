package com.ecoride.trip_service.consumer;

import com.ecoride.trip_service.event.PaymentResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);

    @Bean
    public Consumer<PaymentResultEvent> paymentResultProcessor() {
        return event -> {
            log.info("📩 Respuesta recibida en Trip Service para Reserva ID: {}", event.getReservationId());

            if ("AUTHORIZED".equals(event.getStatus())) {
                log.info("✅ CONFIRMANDO Reserva {} - ¡Viaje asegurado!", event.getReservationId());
                // AQUÍ ACTUALIZARÍAMOS LA ENTIDAD RESERVATION EN LA BD A 'CONFIRMED'
            } else {
                log.warn("❌ CANCELANDO Reserva {} - Falló el pago.", event.getReservationId());
                // AQUÍ ACTUALIZARÍAMOS A 'CANCELLED' Y LIBERARÍAMOS EL ASIENTO
            }
        };
    }
}