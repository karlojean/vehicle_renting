package dev.jeankarlo.vehiclerenting.service.impl;

import java.net.URL;
import java.util.List;

import dev.jeankarlo.vehiclerenting.dto.vehicleImage.VehicleImageResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.entity.VehicleImage;
import dev.jeankarlo.vehiclerenting.mapper.VehicleImageMapper;
import dev.jeankarlo.vehiclerenting.repository.VehicleImageRepository;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import dev.jeankarlo.vehiclerenting.service.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.mapper.VehicleMapper;
import dev.jeankarlo.vehiclerenting.repository.VehicleRepository;
import dev.jeankarlo.vehiclerenting.service.AccountService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;
    private final AccountService accountService;
    private final LocationService locationService;
    private final FileStorageService fileStorageService;
    private final VehicleImageRepository vehicleImageRepository;
    private final VehicleImageMapper vehicleImageMapper;

    public VehicleServiceImpl(VehicleMapper vehicleMapper, VehicleRepository vehicleRepository,
                              AccountService accountService, LocationService locationService, FileStorageService fileStorageService, VehicleImageRepository vehicleImageRepository, VehicleImageMapper vehicleImageMapper) {
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
        this.accountService = accountService;
        this.locationService = locationService;
        this.fileStorageService = fileStorageService;
        this.vehicleImageRepository = vehicleImageRepository;
        this.vehicleImageMapper = vehicleImageMapper;
    }

    @Override
    public VehicleResponseDTO create(Long ownerId, VehicleRequestDTO vehicleCreateDTO) {
        Account account = accountService.getEntityById(ownerId);
        Location location = locationService.getEntityById(vehicleCreateDTO.locationId());
        Vehicle vehicle = vehicleMapper.toEntity(vehicleCreateDTO);

        vehicle.setOwner(account);
        vehicle.setLocation(location);
        vehicle.setIsActive(true);
        return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponseDTO getById(Long ownerId, Long id) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, ownerId);
        return vehicleMapper.toResponseDTO(vehicle);
    }

    @Override
    public List<VehicleResponseDTO> getAll(Long ownerId, Pageable pageable) {
        Account account = accountService.getEntityById(ownerId);
        Page<Vehicle> vehicles = vehicleRepository.findByOwner(account, pageable);
        return vehicles.map(vehicleMapper::toResponseDTO).toList();
    }

    @Override
    public void deleteById(Long id, Long ownerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, ownerId);
        vehicleRepository.delete(vehicle);
    }

    @Override
    public VehicleResponseDTO updateById(Long id, Long ownerId, VehiclePatchDTO vehiclePatchDTO) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, ownerId);

        vehicleMapper.updateVehicle(vehicle, vehiclePatchDTO);

        return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public void deactivate(Long id, Long ownerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, ownerId);
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
    }

    @Override
    public void activate(Long id, Long ownerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, ownerId);
        vehicle.setIsActive(true);
        vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle findVehicleByOwnerOrThrow(Long id, Long ownerId) {
        return vehicleRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    @Override
    public void uploadVehicleImage(Long vehicleId, Long ownerId, MultipartFile file) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(vehicleId, ownerId);

        String url =  fileStorageService.uploadFile(file);

        VehicleImage vehicleImage = new VehicleImage();
        vehicleImage.setUrl(url);
        vehicleImage.setVehicle(vehicle);

        vehicleImageRepository.save(vehicleImage);
    }

    @Override
    public List<VehicleImageResponseDTO> getVehicleImages(Long vehicleId, Long ownerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(vehicleId, ownerId);

        List<VehicleImage> vehicleImages = vehicleImageRepository.findByVehicle(vehicle);

        return vehicleImages.stream().map(vehicleImageMapper::toResponseDTO).toList();
    }
}
