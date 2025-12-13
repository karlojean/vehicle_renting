package dev.jeankarlo.vehiclerenting.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;

public interface VehicleService {
    VehicleResponseDTO create(Long id, VehicleRequestDTO vehicleCreateDTO);

    VehicleResponseDTO getById(Long ownerId, Long vehicleId);

    List<VehicleResponseDTO> getAll(Long ownerId, Pageable pageable);

    void deleteById(Long id, Long ownerId);

    VehicleResponseDTO updateById(Long id, Long ownerId, VehiclePatchDTO vehiclePatchDTO);

    void deactivate(Long id, Long ownerId);

    void activate(Long id, Long ownerId);

    Vehicle findVehicleByOwnerOrThrow(Long id, Long ownerId);
}
