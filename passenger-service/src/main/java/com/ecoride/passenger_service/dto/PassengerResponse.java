package com.ecoride.passenger_service.dto;

public record PassengerResponse(
        Long id,
        String keycloakId,
        String name,
        String email,
        Double ratingAvg
) {}