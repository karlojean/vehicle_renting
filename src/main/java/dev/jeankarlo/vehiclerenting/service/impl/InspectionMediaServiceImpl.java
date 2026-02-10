package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.dto.location.inspectionMedia.InspectionMediaResponseDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleMedia.VehicleMediaResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Inspection;
import dev.jeankarlo.vehiclerenting.entity.InspectionMedia;
import dev.jeankarlo.vehiclerenting.entity.MediaAsset;
import dev.jeankarlo.vehiclerenting.mapper.InspectionMediaMapper;
import dev.jeankarlo.vehiclerenting.repository.InspectionMediaRepository;
import dev.jeankarlo.vehiclerenting.service.InspectionMediaService;
import dev.jeankarlo.vehiclerenting.service.InspectionService;
import dev.jeankarlo.vehiclerenting.service.MediaAssetService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class InspectionMediaServiceImpl implements InspectionMediaService {

    private final InspectionService inspectionService;
    private final MediaAssetService mediaAssetService;
    private final InspectionMediaMapper inspectionMediaMapper;
    private final InspectionMediaRepository inspectionMediaRepository;

    public InspectionMediaServiceImpl(InspectionService inspectionService, MediaAssetService mediaAssetService, InspectionMediaMapper inspectionMediaMapper, InspectionMediaRepository inspectionMediaRepository) {
        this.inspectionService = inspectionService;
        this.mediaAssetService = mediaAssetService;
        this.inspectionMediaMapper = inspectionMediaMapper;
        this.inspectionMediaRepository = inspectionMediaRepository;
    }

    @Override
    @Transactional
    public InspectionMediaResponseDTO uploadMedia(Long inspectionId, Long partnerId, MultipartFile file) {
        Inspection inspection = inspectionService.getInspectionEntityById(inspectionId);

        inspectionService.validatePartnerOwnership(partnerId, inspection);

        MediaAsset mediaAsset = mediaAssetService.uploadAndCreate(file, BucketType.INSPECTIONS);

        InspectionMedia inspectionMedia = new InspectionMedia();
        inspectionMedia.setInspection(inspection);
        inspectionMedia.setMediaAsset(mediaAsset);

        return inspectionMediaMapper.toResponseDTO(inspectionMediaRepository.save(inspectionMedia));
    }

    @Override
    public List<InspectionMediaResponseDTO> getMediasByInspectionId(Long inspectionId) {
        Inspection inspection = inspectionService.getInspectionEntityById(inspectionId);

        List<InspectionMedia> inspectionMedias = inspectionMediaRepository.findAllByInspection(inspection);

        return inspectionMedias.stream().map(
                inspectionMediaMapper::toResponseDTO
        ).toList(
        );
    }

    @Override
    @Transactional
    public void deleteMedia(Long inspectionId, UUID inspectionMediaId, Long partnerId) {
        Inspection inspection = inspectionService.getInspectionEntityById(inspectionId);

        inspectionService.validatePartnerOwnership(partnerId, inspection);

        InspectionMedia inspectionMedia = inspectionMediaRepository.findById(inspectionMediaId)
                .orElseThrow(() -> new RuntimeException("Mídia de inspeção não encontrada."));

        mediaAssetService.deleteFromStorageAndRepository(inspectionMedia.getMediaAsset().getId());
    }
}
