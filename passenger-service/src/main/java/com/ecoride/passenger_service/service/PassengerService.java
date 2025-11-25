package com.ecoride.passenger_service.service;

import com.ecoride.passenger_service.dto.PassengerResponse;

public interface PassengerService {
    PassengerResponse getPassengerProfile(String keycloakId);
}
