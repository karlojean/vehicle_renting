package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleMedia.VehicleMediaResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.MediaAsset;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.VehicleMedia;
import dev.jeankarlo.vehiclerenting.mapper.VehicleMediaMapper;
import dev.jeankarlo.vehiclerenting.repository.VehicleMediaRepository;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import dev.jeankarlo.vehiclerenting.service.MediaAssetService;
import dev.jeankarlo.vehiclerenting.service.VehicleMediaService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class VehicleMediaServiceImpl implements VehicleMediaService {

    private final VehicleService vehicleService;
    private final MediaAssetService mediaAssetService;
    private final VehicleMediaRepository vehicleMediaRepository;
    private final VehicleMediaMapper vehicleMediaMapper;
    private final FileStorageService fileStorageService;

    public VehicleMediaServiceImpl(VehicleService vehicleService, MediaAssetService mediaAssetService, VehicleMediaRepository vehicleMediaRepository, VehicleMediaMapper vehicleMediaMapper, FileStorageService fileStorageService) {
        this.vehicleService = vehicleService;
        this.mediaAssetService = mediaAssetService;
        this.vehicleMediaRepository = vehicleMediaRepository;
        this.vehicleMediaMapper = vehicleMediaMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public VehicleMediaResponseDTO uploadMedia(Long vehicleId, Long partnerId, MultipartFile file) {
        Vehicle vehicle = vehicleService.findVehicleByOwnerOrThrow(vehicleId, partnerId);
        MediaAsset mediaAsset = mediaAssetService.uploadAndCreate(file, BucketType.VEHICLES);

        VehicleMedia vehicleMedia = new VehicleMedia();
        vehicleMedia.setVehicle(vehicle);
        vehicleMedia.setMediaAssets(mediaAsset);

        return vehicleMediaMapper.toResponseDTO(vehicleMediaRepository.save(vehicleMedia));
    }

    @Override
    public List<VehicleMediaResponseDTO> getVehicleMedias(Long vehicleId, Long partnerId) {
        Vehicle vehicle = vehicleService.getEntityById(vehicleId);

        var vehicleMedias = vehicleMediaRepository.findAllByVehicle(vehicle);

        return vehicleMedias.stream()
                .map(vehicleMediaMapper::toResponseDTO)
                .toList();
    }
}
