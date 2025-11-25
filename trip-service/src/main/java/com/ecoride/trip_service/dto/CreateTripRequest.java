package com.ecoride.trip_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTripRequest(
        String origin,
        String destination,
        LocalDateTime departureTime,
        Integer seatsAvailable,
        BigDecimal price
) {}
