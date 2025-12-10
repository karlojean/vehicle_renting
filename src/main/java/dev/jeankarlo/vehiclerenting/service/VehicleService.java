package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;

import java.awt.print.Pageable;
import java.util.List;

public interface VehicleService {
    VehicleResponseDTO create(VehicleCreateDTO vehicleCreateDTO);
    VehicleResponseDTO getById(Long id);
    List<VehicleResponseDTO> getAll();
    void deleteById(Long id);
    VehicleResponseDTO update(Long id, VehiclePatchDTO vehiclePatchDTO);

}
