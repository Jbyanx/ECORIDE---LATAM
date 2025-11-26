package com.ecoride.trip_service.service;

import com.ecoride.trip_service.dto.CreateTripRequest;
import com.ecoride.trip_service.dto.TripResponse;
import java.util.List;

public interface TripService {

    TripResponse createTrip(CreateTripRequest request, String userId);

    List<TripResponse> getAllTrips();

    void createReservation(Long tripId, String passengerId);
}