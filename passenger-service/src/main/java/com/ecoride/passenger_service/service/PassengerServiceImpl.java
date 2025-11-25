package com.ecoride.passenger_service.service;

import com.ecoride.passenger_service.dto.PassengerResponse;
import com.ecoride.passenger_service.mapper.PassengerMapper;
import com.ecoride.passenger_service.model.Passenger;
import com.ecoride.passenger_service.repository.PassengerRepository;
import com.ecoride.passenger_service.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository repository;
    private final PassengerMapper mapper;

    @Override
    @Transactional
    public PassengerResponse getPassengerProfile(String keycloakId) {
        // Busca al pasajero por su ID de Keycloak
        return repository.findByKeycloakId(keycloakId)
                .map(mapper::toResponse)
                .orElseGet(() -> createNewPassenger(keycloakId));
    }

    private PassengerResponse createNewPassenger(String keycloakId) {
        // Lógica de auto-registro (On-the-fly)
        // En un caso real, podríamos llamar a Keycloak para pedir el email/nombre real.
        // Por ahora, usamos placeholders basados en el ID.
        Passenger newPassenger = Passenger.builder()
                .keycloakId(keycloakId)
                .name(keycloakId) // Temporal
                .email(keycloakId + "@ecoride.com") // Temporal
                .ratingAvg(5.0) // Todos empiezan con 5 estrellas
                .build();

        Passenger saved = repository.save(newPassenger);
        return mapper.toResponse(saved);
    }
}