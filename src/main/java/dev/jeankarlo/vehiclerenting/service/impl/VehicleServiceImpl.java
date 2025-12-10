package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.mapper.VehicleMapper;
import dev.jeankarlo.vehiclerenting.repository.VehicleRepository;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleMapper vehicleMapper, VehicleRepository vehicleRepository) {
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponseDTO create(VehicleCreateDTO vehicleCreateDTO) {
        Vehicle vehicle = vehicleMapper.toEntity(vehicleCreateDTO);
        return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    }

    public VehicleResponseDTO getById(Long id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public List<VehicleResponseDTO> getAll() {
        return vehicleRepository.findAll().stream().map(vehicleMapper::toResponseDTO).toList();
    }

    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }

    public VehicleResponseDTO update(Long id, VehiclePatchDTO vehiclePatchDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicleMapper.updateVehicle(vehicle, vehiclePatchDTO);

        return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    }
}
