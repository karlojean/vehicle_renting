package dev.jeankarlo.vehiclerenting.controller;

import dev.jeankarlo.vehiclerenting.dto.location.LocationRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.location.LocationResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
@PreAuthorize("hasRole('PARTNER')")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LocationResponseDTO> create(
            @RequestBody @Valid LocationRequestDTO locationRequestDTO,
            @AuthenticationPrincipal Account account
            ) {
        Long partnerId = account.getId();
        return  ResponseEntity.ok(locationService.create(locationRequestDTO, partnerId));
    }

    @GetMapping
    public ResponseEntity<?> getAllByPartner(
            @AuthenticationPrincipal Account account
    ) {
        Long partnerId = account.getId();
        return ResponseEntity.ok(locationService.getAllLocationsByPartnerId(partnerId));
    }
}
