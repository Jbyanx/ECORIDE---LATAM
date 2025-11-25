package com.ecoride.passenger_service.mapper;

import com.ecoride.passenger_service.dto.PassengerResponse;
import com.ecoride.passenger_service.model.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassengerMapper {
    PassengerResponse toResponse(Passenger passenger);
}