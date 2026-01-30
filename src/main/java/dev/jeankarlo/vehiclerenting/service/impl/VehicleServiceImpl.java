package dev.jeankarlo.vehiclerenting.service.impl;

import java.io.IOException;
import java.util.List;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleSearchFilter;
import dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleImage.VehicleImageResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.*;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.mapper.VehicleImageMapper;
import dev.jeankarlo.vehiclerenting.repository.VehicleMediaRepository;
import dev.jeankarlo.vehiclerenting.service.*;
import dev.jeankarlo.vehiclerenting.specifications.VehicleSpec;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.mapper.VehicleMapper;
import dev.jeankarlo.vehiclerenting.repository.VehicleRepository;
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
    private final MediaAssetService mediaAssetService;
    private final VehicleMediaRepository vehicleMediaRepository;

    public VehicleServiceImpl(VehicleMapper vehicleMapper, VehicleRepository vehicleRepository,
                              AccountService accountService, LocationService locationService, FileStorageService fileStorageService, VehicleImageRepository vehicleImageRepository, VehicleImageMapper vehicleImageMapper, MediaAssetService mediaAssetService, VehicleMediaRepository vehicleMediaRepository) {
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
        this.accountService = accountService;
        this.locationService = locationService;
        this.fileStorageService = fileStorageService;
        this.vehicleImageRepository = vehicleImageRepository;
        this.vehicleImageMapper = vehicleImageMapper;
        this.mediaAssetService = mediaAssetService;
        this.vehicleMediaRepository = vehicleMediaRepository;
    }

    @Override
    @Transactional
    public VehicleResponseDTO create(Long partnerId, VehicleRequestDTO vehicleCreateDTO) {
        Account account = accountService.getEntityById(partnerId);
        Location location = locationService.getEntityById(vehicleCreateDTO.locationId());
        Vehicle vehicle = vehicleMapper.toEntity(vehicleCreateDTO);

        vehicle.setPartner(account);
        vehicle.setLocation(location);
        vehicle.setIsActive(true);
        return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponseDTO getById(Long partnerId, Long id) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, partnerId);
        return vehicleMapper.toResponseDTO(vehicle);
    }

    @Override
    public List<VehicleResponseDTO> getAll(Long partnerId, Pageable pageable) {
        Account account = accountService.getEntityById(partnerId);
        Page<Vehicle> vehicles = vehicleRepository.findByPartner(account, pageable);
        return vehicles.map(vehicleMapper::toResponseDTO).toList();
    }

    @Override
    public void deleteById(Long id, Long partnerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, partnerId);
        vehicleRepository.delete(vehicle);
    }

    @Override
    public VehicleResponseDTO updateById(Long id, Long partnerId, VehiclePatchDTO vehiclePatchDTO) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, partnerId);

        vehicleMapper.updateVehicle(vehicle, vehiclePatchDTO);

        return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public void deactivate(Long id, Long partnerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, partnerId);
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
    }

    @Override
    public void activate(Long id, Long partnerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(id, partnerId);
        vehicle.setIsActive(true);
        vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle findVehicleByOwnerOrThrow(Long id, Long partnerId) {
        return vehicleRepository.findByIdAndPartner_Id(id, partnerId)
                .orElseThrow(() -> new BusinessException("Veiculo não encontrado ou não pertence ao usuário.", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public VehicleImageResponseDTO uploadVehicleImage(Long vehicleId, Long partnerId, MultipartFile file) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(vehicleId, partnerId);

        try {
            MediaAsset mediaAsset = mediaAssetService.uploadAndCreate(file, BucketType.VEHICLES);
            VehicleMedia vehicleMedia = new VehicleMedia();
            vehicleMedia.setVehicle(vehicle);
            vehicleMedia.setMediaAssets(mediaAsset);

            vehicleMediaRepository.save(vehicleMedia);

            String url = fileStorageService.getPublicUrl(mediaAsset.getStoragePath(), BucketType.VEHICLES);

            VehicleImageResponseDTO vehicleImage = new VehicleImageResponseDTO(vehicle.getId(), url);
            return vehicleImage;
        } catch (IOException e) {
            throw new BusinessException("Erro ao fazer upload da imagem do veículo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<VehicleImageResponseDTO> getVehicleImages(Long vehicleId, Long partnerId) {
        Vehicle vehicle = findVehicleByOwnerOrThrow(vehicleId, partnerId);

        List<VehicleImage> vehicleImages = vehicleImageRepository.findByVehicle(vehicle);

        return vehicleImages.stream().map(image -> {
            String url = "refactor";
            return new VehicleImageResponseDTO(image.getId(), url);
        }).toList();
    }

    @Override
    public Vehicle getEntityById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new BusinessException("Veiculo com o ID: " + vehicleId + " não encontrado.", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<VehicleResponseDTO> findAvailableVehicle(VehicleSearchFilter vehicleSearchFilter) {
        Specification<Vehicle> spec = VehicleSpec.hasCity(vehicleSearchFilter.city()).and(VehicleSpec.isAvailable(vehicleSearchFilter.startDate(), vehicleSearchFilter.endDate()));

        List<Vehicle> vehicles = vehicleRepository.findAll(spec);

        return vehicles.stream()
                .map(vehicleMapper::toResponseDTO)
                .toList();
    }
}
