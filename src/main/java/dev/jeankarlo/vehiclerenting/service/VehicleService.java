package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {
    VehicleResponseDTO create(Long id, VehicleCreateDTO vehicleCreateDTO);
    VehicleResponseDTO getById(Long ownerId, Long vehicleId);
    List<VehicleResponseDTO> getAll(Long ownerId, Pageable pageable);
    void deleteById(Long id, Long ownerId);
    VehicleResponseDTO updateById(Long id, Long ownerId, VehiclePatchDTO vehiclePatchDTO);
}
