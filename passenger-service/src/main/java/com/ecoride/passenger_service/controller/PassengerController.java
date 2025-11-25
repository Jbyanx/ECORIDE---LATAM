package com.ecoride.passenger_service.controller;

import com.ecoride.passenger_service.dto.PassengerResponse;
import com.ecoride.passenger_service.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService service;

    @GetMapping("/me")
    public PassengerResponse getMyProfile(@RequestHeader("X-User-Id") String userId) {
        return service.getPassengerProfile(userId);
    }
}