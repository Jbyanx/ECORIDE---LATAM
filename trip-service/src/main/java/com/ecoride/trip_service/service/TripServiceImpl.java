package com.ecoride.trip_service.service.impl;

import com.ecoride.trip_service.dto.CreateTripRequest;
import com.ecoride.trip_service.dto.TripResponse;
import com.ecoride.trip_service.event.ReservationEvent;
import com.ecoride.trip_service.mapper.TripMapper;
import com.ecoride.trip_service.model.Reservation;
import com.ecoride.trip_service.model.Trip;
import com.ecoride.trip_service.repository.ReservationRepository;
import com.ecoride.trip_service.repository.TripRepository;
import com.ecoride.trip_service.service.TripService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final ReservationRepository reservationRepository;
    private final StreamBridge streamBridge; // Mover StreamBridge del Controller al Service
    private final TripRepository repository;
    private final TripMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Override
    @Transactional
    public void createReservation(Long tripId, String passengerId) {
        // 1. Buscar el viaje y validar
        Trip trip = repository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        if (trip.getSeatsAvailable() <= 0) {
            throw new RuntimeException("No hay asientos disponibles");
        }

        // 2. Crear la reserva en estado PENDING (Patrón Saga: Transacción Local)
        Reservation reservation = Reservation.builder()
                .trip(trip)
                .passengerId(passengerId)
                .status(Reservation.ReservationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // 3. Publicar Evento (Saga: Evento) - Usando el PRECIO REAL del viaje
        ReservationEvent event = new ReservationEvent();
        event.setReservationId(savedReservation.getId());
        event.setTripId(trip.getId());
        event.setPassengerId(passengerId);
        event.setAmount(trip.getPrice()); // <---  Usamos el precio de la BD de ese trip
        event.setStatus("PENDING");

        logger.info("📤 Iniciando Saga para Reserva ID: {}, Monto: {}", savedReservation.getId(), trip.getPrice());
        streamBridge.send("reservation-out-0", event);
    }
}