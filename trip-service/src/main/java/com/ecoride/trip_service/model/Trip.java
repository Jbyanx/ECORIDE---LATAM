package com.ecoride.trip_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un viaje publicado por un conductor en la plataforma Eco-Ride.
 * Esta entidad gestiona la información del trayecto, disponibilidad y estado.
 */
@Entity
@Table(name = "trips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    /**
     * Identificador único del viaje en la base de datos (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del usuario conductor (viene de Keycloak/Identity Service).
     * Permite vincular este viaje con el perfil del conductor en PassengerService.
     */
    @Column(name = "driver_id", nullable = false)
    private String driverId;

    /**
     * Punto de partida del viaje (ej. "Universidad del magdalena", "Centro").
     */
    @Column(nullable = false)
    private String origin;

    /**
     * Punto de llegada del viaje (ej. "Carrera 5ta", "buenavista").
     */
    @Column(nullable = false)
    private String destination;

    /**
     * Fecha y hora exacta de salida.
     * Crucial para que los pasajeros busquen viajes futuros.
     */
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    /**
     * Cantidad actual de asientos libres.
     * Se debe decrementar cada vez que se confirma una reserva.
     */
    @Column(name = "seats_available", nullable = false)
    private Integer seatsAvailable;

    /**
     * Costo simbólico por asiento para compartir gastos (gasolina/peajes).
     * Se usa BigDecimal para evitar errores de redondeo en dinero.
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Estado actual del ciclo de vida del viaje.
     * CREATED: Publicado y visible.
     * FULL: Sin asientos (no aparece en búsquedas).
     * COMPLETED: El viaje finalizó (permite calificar).
     * CANCELLED: El conductor lo canceló (dispara reembolsos).
     */
    @Enumerated(EnumType.STRING)
    private TripStatus status;

    public enum TripStatus {
        CREATED, FULL, COMPLETED, CANCELLED
    }
}
