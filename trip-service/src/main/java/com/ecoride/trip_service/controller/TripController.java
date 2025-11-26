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

    @PostMapping("/{tripId}/reservations") // Ajustamos la ruta según PDF
    @ResponseStatus(HttpStatus.ACCEPTED) // 202 Accepted es ideal para procesos asíncronos
    public void createReservation(
            @PathVariable Long tripId,
            @RequestHeader(value = "X-User-Id") String passengerId
    ) {
        service.createReservation(tripId, passengerId);
    }

}