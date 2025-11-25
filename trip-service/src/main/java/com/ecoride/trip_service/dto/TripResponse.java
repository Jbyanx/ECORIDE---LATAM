package com.ecoride.trip_service.dto;

import com.ecoride.trip_service.model.Trip;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Usamos record para DTOs inmutables (Java 21)
public record TripResponse(
        Long id,
        String driverId,
        String origin,
        String destination,
        LocalDateTime departureTime,
        Integer seatsAvailable,
        BigDecimal price,
        Trip.TripStatus status
) {}
