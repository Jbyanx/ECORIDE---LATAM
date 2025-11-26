package com.ecoride.trip_service.controller;

import com.ecoride.trip_service.dto.CreateTripRequest;
import com.ecoride.trip_service.dto.TripResponse;
import com.ecoride.trip_service.event.ReservationEvent; // <--- Importar el evento
import com.ecoride.trip_service.service.TripService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge; // <--- Importante
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService service;
    private final StreamBridge streamBridge; // <--- Inyectamos el puente de mensajería
    private static final Logger log = LoggerFactory.getLogger(TripController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(
            @RequestBody CreateTripRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        return service.createTrip(request, userId);
    }

    @GetMapping
    public List<TripResponse> getAllTrips() {
        return service.getAllTrips();
    }

    // --- ENDPOINT DE PRUEBA PARA LA SAGA ---
    @PostMapping("/{tripId}/reserve")
    public String reserveSeat(@PathVariable Long tripId, @RequestHeader(value = "X-User-Id", defaultValue = "test-passenger") String passengerId,
                              @RequestParam(required = false) java.math.BigDecimal amount) {

        //si no se envia monto se usa este
        BigDecimal finalAmount = (amount != null) ? amount : new BigDecimal("15000.00");
        // 1. Simulamos datos de la reserva
        ReservationEvent event = new ReservationEvent();
        event.setReservationId(System.currentTimeMillis()); // ID aleatorio por ahora
        event.setTripId(tripId);
        event.setPassengerId(passengerId);
        event.setAmount(amount);
        event.setStatus("PENDING");

        log.info("📤 Enviando evento de reserva para el viaje: {}", tripId);

        // 2. ¡DISPARO! Enviamos el mensaje al canal definido en el YAML
        // "reservation-out-0" debe coincidir con lo que pusimos en trip-service.yml
        boolean sent = streamBridge.send("reservation-out-0", event);

        return sent ? "¡Evento enviado a RabbitMQ! 🐰" : "Error al enviar evento ❌";
    }
}