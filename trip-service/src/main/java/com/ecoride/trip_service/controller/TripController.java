package com.ecoride.trip_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripController {

    @GetMapping("/ping")
    public String ping() {
        return "¡Hola! Soy Trip Service respondiendo desde el puerto protegido.";
    }
}