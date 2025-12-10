package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.mapper.VehicleMapper;
import dev.jeankarlo.vehiclerenting.repository.VehicleRepository;
import dev.jeankarlo.vehiclerenting.service.AccountService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;
    private final AccountService accountService;

    public VehicleServiceImpl(VehicleMapper vehicleMapper, VehicleRepository vehicleRepository, AccountService accountService) {
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
        this.accountService = accountService;
    }

    public VehicleResponseDTO create(Long ownerId, VehicleCreateDTO vehicleCreateDTO) {
        Account account = accountService.getEntityById(ownerId);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleCreateDTO);
        vehicle.setOwner(account);
        return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    }

    public VehicleResponseDTO getById(Long ownerId, Long id) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, ownerId);
        return vehicleMapper.toResponseDTO(vehicle);
    }

    public List<VehicleResponseDTO> getAll(Long ownerId, Pageable pageable) {
        Account account = accountService.getEntityById(ownerId);
        Page<Vehicle> vehicles = vehicleRepository.findByOwner(account, pageable);
        return vehicles.map(vehicleMapper::toResponseDTO).toList();
    }

    public void deleteById(Long id, Long ownerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, ownerId);
        vehicleRepository.delete(vehicle);
    }

    public VehicleResponseDTO updateById(Long id, Long ownerId, VehiclePatchDTO vehiclePatchDTO) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, ownerId);

        vehicleMapper.updateVehicle(vehicle, vehiclePatchDTO);

        return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    }

    private Vehicle findVehicleByOwnerOrThrow(Long id, Long ownerId) {
        return vehicleRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }
}
