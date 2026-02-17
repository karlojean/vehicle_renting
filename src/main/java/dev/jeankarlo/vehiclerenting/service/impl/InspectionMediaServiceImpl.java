package dev.jeankarlo.vehiclerenting.service.impl;

import java.util.List;
import java.util.UUID;

import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.dto.inspection.inspectionMedia.InspectionMediaResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Inspection;
import dev.jeankarlo.vehiclerenting.entity.InspectionMedia;
import dev.jeankarlo.vehiclerenting.entity.MediaAsset;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.mapper.InspectionMediaMapper;
import dev.jeankarlo.vehiclerenting.repository.InspectionMediaRepository;
import dev.jeankarlo.vehiclerenting.service.InspectionMediaService;
import dev.jeankarlo.vehiclerenting.service.InspectionService;
import dev.jeankarlo.vehiclerenting.service.MediaAssetService;
import jakarta.transaction.Transactional;

@Service
public class InspectionMediaServiceImpl implements InspectionMediaService {

    private static final List<String> ALLOWED_MEDIA_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "video/mp4",
            "video/mpeg");

    private final InspectionService inspectionService;
    private final MediaAssetService mediaAssetService;
    private final InspectionMediaMapper inspectionMediaMapper;
    private final InspectionMediaRepository inspectionMediaRepository;

    public InspectionMediaServiceImpl(InspectionService inspectionService, MediaAssetService mediaAssetService,
            InspectionMediaMapper inspectionMediaMapper, InspectionMediaRepository inspectionMediaRepository) {
        this.inspectionService = inspectionService;
        this.mediaAssetService = mediaAssetService;
        this.inspectionMediaMapper = inspectionMediaMapper;
        this.inspectionMediaRepository = inspectionMediaRepository;
    }

    @Override
    @Transactional
    public InspectionMediaResponseDTO uploadMedia(Long inspectionId, Long partnerId, MultipartFile file) {

        if (!ALLOWED_MEDIA_TYPES.contains(file.getContentType())) {
            throw new BusinessException("Tipo de mídia não suportado.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        Inspection inspection = inspectionService.getInspectionEntityById(inspectionId);

        if(inspection.getStatus() != InspectionStatus.PENDING) {
            throw new BusinessException("Mídias só podem ser adicionadas a inspeções com status PENDING.", HttpStatus.BAD_REQUEST);
        }

        if (inspectionMediaRepository.countByInspection(inspection) >= 20) {
            throw new BusinessException("Número máximo de mídias atingido para esta inspeção.", HttpStatus.BAD_REQUEST);
        }

        inspectionService.validatePartnerOwnership(partnerId, inspection);

        MediaAsset mediaAsset = mediaAssetService.uploadAndCreate(file, BucketType.INSPECTIONS);

        InspectionMedia inspectionMedia = new InspectionMedia();
        inspectionMedia.setInspection(inspection);
        inspectionMedia.setMediaAsset(mediaAsset);

        return inspectionMediaMapper.toResponseDTO(inspectionMediaRepository.save(inspectionMedia));
    }

    @Override
    public List<InspectionMediaResponseDTO> getMediasByInspectionId(Long inspectionId, Long partnerId) {
        Inspection inspection = inspectionService.getInspectionEntityById(inspectionId);

        inspectionService.validatePartnerOwnership(partnerId, inspection);

        List<InspectionMedia> inspectionMedias = inspectionMediaRepository.findAllByInspection(inspection);

        return inspectionMedias.stream().map(
                inspectionMediaMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public void deleteMedia(Long inspectionId, UUID inspectionMediaId, Long partnerId) {
        Inspection inspection = inspectionService.getInspectionEntityById(inspectionId);

        inspectionService.validatePartnerOwnership(partnerId, inspection);

        InspectionMedia inspectionMedia = inspectionMediaRepository.findByIdAndInspection(inspectionMediaId, inspection)
                .orElseThrow(() -> new BusinessException("Mídia de inspeção não encontrada.", HttpStatus.NOT_FOUND));

        mediaAssetService.deleteFromStorageAndRepository(inspectionMedia.getMediaAsset().getId());
    }
}
