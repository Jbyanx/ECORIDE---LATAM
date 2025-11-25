package com.ecoride.passenger_service.repository;

import com.ecoride.passenger_service.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByKeycloakId(String keycloakId);
}
