package dev.jeankarlo.vehiclerenting.controller;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@PreAuthorize("hasRole('RENTING_PARTNER')")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponseDTO create(
            @RequestBody @Valid VehicleCreateDTO createVehicleDTO,
            @AuthenticationPrincipal Account account
    ) {
        Long userId = account.getId();
        return vehicleService.create(userId, createVehicleDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account
    ){
        Long userId = account.getId();
        return ResponseEntity.ok(vehicleService.getById(userId, id));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue =  "10") int size,
            @AuthenticationPrincipal Account account
    ){
        Long userId = account.getId();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(vehicleService.getAll(userId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account
    ){
        Long userId = account.getId();
        vehicleService.deleteById(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable Long id,
            @RequestBody @Valid VehiclePatchDTO vehiclePatchDTO,
            @AuthenticationPrincipal Account account
    ) {
        Long ownerId = account.getId();
        return ResponseEntity.ok(vehicleService.updateById(id, ownerId, vehiclePatchDTO));
    }

    @PatchMapping("/{id}/deactivate")
    public  ResponseEntity<Void>  deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account
    ) {
        Long ownerId = account.getId();
        vehicleService.deactivate(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public  ResponseEntity<Void>  activate(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account
    ) {
        Long ownerId = account.getId();
        vehicleService.activate(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
