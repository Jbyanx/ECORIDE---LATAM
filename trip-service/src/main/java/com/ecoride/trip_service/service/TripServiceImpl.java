package com.ecoride.trip_service.service.impl;

import com.ecoride.trip_service.dto.CreateTripRequest;
import com.ecoride.trip_service.dto.TripResponse;
import com.ecoride.trip_service.mapper.TripMapper;
import com.ecoride.trip_service.model.Trip;
import com.ecoride.trip_service.repository.TripRepository;
import com.ecoride.trip_service.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // ¡Esta anotación va AQUÍ, en la implementación!
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository repository;
    private final TripMapper mapper;

    @Override
    @Transactional
    public TripResponse createTrip(CreateTripRequest request, String userId) {
        // 1. Convertir
        Trip newTrip = mapper.toEntity(request);

        // 2. Lógica de Negocio
        newTrip.setDriverId(userId != null ? userId : "test-driver-id");
        newTrip.setStatus(Trip.TripStatus.CREATED);

        // 3. Persistir
        Trip savedTrip = repository.save(newTrip);

        // 4. Responder
        return mapper.toResponse(savedTrip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}