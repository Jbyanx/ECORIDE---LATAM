package com.ecoride.trip_service.mapper;

import com.ecoride.trip_service.dto.CreateTripRequest;
import com.ecoride.trip_service.dto.TripResponse;
import com.ecoride.trip_service.model.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring", // Permite inyectarlo con @Autowired
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Ignora campos que no coincidan (lo que pediste)
)
public interface TripMapper {

    // De DTO Entrada -> Entidad
    // Ignoramos ID y Status porque se generan en el backend
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "driverId", ignore = true) // Lo seteamos manual desde el token
    Trip toEntity(CreateTripRequest request);

    // De Entidad -> DTO Salida
    TripResponse toResponse(Trip trip);
}