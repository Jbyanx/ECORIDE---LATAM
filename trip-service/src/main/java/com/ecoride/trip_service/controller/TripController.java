package com.ecoride.trip_service.controller;

import com.ecoride.trip_service.dto.CreateTripRequest;
import com.ecoride.trip_service.dto.TripResponse;
import com.ecoride.trip_service.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(
            @RequestBody CreateTripRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        // El controlador solo delega. No sabe de entidades ni de lógica.
        return service.createTrip(request, userId);
    }

    @GetMapping
    public List<TripResponse> getAllTrips() {
        return service.getAllTrips();
    }
}