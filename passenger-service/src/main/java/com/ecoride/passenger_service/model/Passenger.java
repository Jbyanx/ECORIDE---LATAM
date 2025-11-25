package com.ecoride.passenger_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passengers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El ID que viene de Keycloak (sub o preferred_username)
    @Column(name = "keycloak_id", unique = true, nullable = false)
    private String keycloakId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    // Se actualiza cuando termina un viaje
    @Column(name = "rating_avg")
    private Double ratingAvg;
}
