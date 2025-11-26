package com.ecoride.trip_service.repository;

import com.ecoride.trip_service.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}