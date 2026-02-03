package dev.jeankarlo.vehiclerenting.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleMedia.VehicleMediaResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.MediaAsset;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.VehicleMedia;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.mapper.VehicleMediaMapper;
import dev.jeankarlo.vehiclerenting.repository.VehicleMediaRepository;
import dev.jeankarlo.vehiclerenting.service.MediaAssetService;
import dev.jeankarlo.vehiclerenting.service.VehicleMediaService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import jakarta.transaction.Transactional;

@Service
public class VehicleMediaServiceImpl implements VehicleMediaService {

    private static final List<String> ALLOWED_MEDIA_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "video/mp4",
            "video/mpeg");

    private final VehicleService vehicleService;
    private final MediaAssetService mediaAssetService;
    private final VehicleMediaRepository vehicleMediaRepository;
    private final VehicleMediaMapper vehicleMediaMapper;

    public VehicleMediaServiceImpl(VehicleService vehicleService, MediaAssetService mediaAssetService,
            VehicleMediaRepository vehicleMediaRepository, VehicleMediaMapper vehicleMediaMapper) {
        this.vehicleService = vehicleService;
        this.mediaAssetService = mediaAssetService;
        this.vehicleMediaRepository = vehicleMediaRepository;
        this.vehicleMediaMapper = vehicleMediaMapper;
    }

    @Override
    @Transactional
    public VehicleMediaResponseDTO uploadMedia(Long vehicleId, Long partnerId, MultipartFile file) {

        if (!ALLOWED_MEDIA_TYPES.contains(file.getContentType())) {
            throw new BusinessException("Tipo de mídia não suportado.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        Vehicle vehicle = vehicleService.findVehicleByOwnerOrThrow(vehicleId, partnerId);

        if (vehicleMediaRepository.countByVehicle(vehicle) >= 10) {
            throw new BusinessException("Número máximo de mídias atingido para este veículo.", HttpStatus.BAD_REQUEST);
        }

        MediaAsset mediaAsset = mediaAssetService.uploadAndCreate(file, BucketType.VEHICLES);

        VehicleMedia vehicleMedia = new VehicleMedia();
        vehicleMedia.setVehicle(vehicle);
        vehicleMedia.setMediaAsset(mediaAsset);

        return vehicleMediaMapper.toResponseDTO(vehicleMediaRepository.save(vehicleMedia));
    }

    @Override
    public List<VehicleMediaResponseDTO> getVehicleMedias(Long vehicleId) {
        Vehicle vehicle = vehicleService.getEntityById(vehicleId);

        var vehicleMedias = vehicleMediaRepository.findAllByVehicle(vehicle);

        return vehicleMedias.stream()
                .map(vehicleMediaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteMedia(Long vehicleId, UUID vehicleMediaId, Long partnerId) {
        Vehicle vehicle = vehicleService.findVehicleByOwnerOrThrow(vehicleId, partnerId);

        VehicleMedia vehicleMedia = vehicleMediaRepository.findByIdAndVehicle(vehicleMediaId, vehicle)
                .orElseThrow(() -> new BusinessException("Mídia do veículo não encontrada.", HttpStatus.NOT_FOUND));

        // VehicleMedia será deletado em cascata pelo banco (ON DELETE CASCADE)
        mediaAssetService.deleteFromStorageAndRepository(vehicleMedia.getMediaAsset().getId());
    }
}
