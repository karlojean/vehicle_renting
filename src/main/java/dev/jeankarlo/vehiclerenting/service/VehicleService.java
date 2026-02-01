package dev.jeankarlo.vehiclerenting.service;

import java.util.List;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleSearchFilter;
import org.springframework.data.domain.Pageable;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;

public interface VehicleService {
    VehicleResponseDTO create(Long id, VehicleRequestDTO vehicleCreateDTO);

    VehicleResponseDTO getById(Long partnerId, Long vehicleId);

    List<VehicleResponseDTO> getAll(Long partnerId, Pageable pageable);

    void deleteById(Long id, Long partnerId);

    VehicleResponseDTO updateById(Long id, Long partnerId, VehiclePatchDTO vehiclePatchDTO);

    void deactivate(Long id, Long partnerId);

    void activate(Long id, Long partnerId);

    Vehicle findVehicleByOwnerOrThrow(Long id, Long partnerId);

    Vehicle getEntityById(Long vehicleId);

    List<VehicleResponseDTO> findAvailableVehicle(VehicleSearchFilter vehicleSearchFilter);
}
