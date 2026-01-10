package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionPatchDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionResponseDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.inspectionImage.InspectionImageRespondeDTO;
import dev.jeankarlo.vehiclerenting.entity.*;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.mapper.InspectionMapper;
import dev.jeankarlo.vehiclerenting.repository.InspectionImageRepository;
import dev.jeankarlo.vehiclerenting.repository.InspectionRepository;
import dev.jeankarlo.vehiclerenting.service.BookingService;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import dev.jeankarlo.vehiclerenting.service.InspectionService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InspectionServiceImpl implements InspectionService {

    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final InspectionRepository inspectionRepository;
    private final FileStorageService fileStorageService;
    private final InspectionImageRepository inspectionImageRepository;
    private final InspectionMapper inspectionMapper;

    public InspectionServiceImpl(BookingService bookingService, VehicleService vehicleService, InspectionRepository inspectionRepository, FileStorageService fileStorageService, InspectionImageRepository inspectionImageRepository, InspectionMapper inspectionMapper) {
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.inspectionRepository = inspectionRepository;
        this.fileStorageService = fileStorageService;
        this.inspectionImageRepository = inspectionImageRepository;
        this.inspectionMapper = inspectionMapper;
    }

    @Override
    @Transactional
    public InspectionResponseDTO initInspection(InspectionInitDTO inspectionInitDTO, Long partnerId) {
        Booking booking = bookingService.getEntityById(inspectionInitDTO.bookingId());

        vehicleService.findVehicleByOwnerOrThrow(booking.getVehicle().getId(), partnerId);

        boolean existsInspectionOfType = inspectionRepository.existsByBookingAndTypeAndStatusNot(
                booking,
                inspectionInitDTO.type(),
                InspectionStatus.CANCELLED
        );

        if(existsInspectionOfType) {
            throw new BusinessException("Já existe uma inspeção do tipo " + inspectionInitDTO.type() + " para esta reserva.", HttpStatus.BAD_REQUEST);
        }

        if (inspectionInitDTO.type().equals(InspectionType.PICK_UP) && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException("A abertura de inspeção para retirada só pode ser feita em reservas confirmadas.", HttpStatus.BAD_REQUEST);
        }

        if (inspectionInitDTO.type().equals(InspectionType.DROP_OFF) && booking.getStatus() != BookingStatus.ACTIVE) {
            throw new BusinessException("A abertura de inspeção para devolução só pode ser feita em reservas ativas.", HttpStatus.BAD_REQUEST);
        }


        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(inspectionInitDTO.type());
        inspection.setStatus(InspectionStatus.PENDING);
        inspection.setInspectionDate(Instant.now());

        inspectionRepository.save(inspection);

        return inspectionMapper.toResponseDTO(inspection);
    }


    @Override
    public InspectionImageRespondeDTO uploadInspectionImage(Long inspectionId, MultipartFile file, Long partnerId) {
        Inspection inspection = getInspectionEntityById(inspectionId);

        validatePartnerOwnership(partnerId, inspection);

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null) extension = "jpg";

        String key = "inspections/" + inspectionId + "/" + UUID.randomUUID() + "." + extension;

        String url = fileStorageService.upload(BucketType.INSPECTIONS, key, file);

        InspectionImage inspectionImage = new InspectionImage();
        inspectionImage.setFileKey(key);
        inspectionImage.setInspection(inspection);

        inspectionImageRepository.save(inspectionImage);

        return new InspectionImageRespondeDTO(
                inspectionImage.getId(),
                url
        );
    }

    @Override
    public List<InspectionImageRespondeDTO> getInspectionImagesById(Long inspectionId, Long partnerId) {
        Inspection inspection = getInspectionEntityById(inspectionId);
        validatePartnerOwnership(partnerId, inspection);

        List<InspectionImage> images = inspectionImageRepository.findByInspection(inspection);

        return images.stream()
                .map(image -> {
                    String url = fileStorageService.getUrl(BucketType.INSPECTIONS, image.getFileKey());
                    return new InspectionImageRespondeDTO(image.getId(), url);
                })
                .toList();
    }

    @Override
    @Transactional
    public InspectionResponseDTO updateById(Long id, InspectionPatchDTO inspectionPatchDTO, Long partnerId) {
        Inspection inspection = getInspectionEntityById(id);

        validatePartnerOwnership(partnerId, inspection);

        if(inspection.getStatus() != InspectionStatus.PENDING) {
            throw new BusinessException("Apenas inspeções com status PENDENTE podem ser atualizadas.", HttpStatus.BAD_REQUEST);
        }

        inspectionMapper.updateInspection(inspection, inspectionPatchDTO);

        inspectionRepository.save(inspection);
        return inspectionMapper.toResponseDTO(inspection);
    }

    @Override
    public InspectionResponseDTO completeInspection(Long id, Long partnerId) {
        Inspection inspection = getInspectionEntityById(id);

        validatePartnerOwnership(partnerId, inspection);

        if(inspection.getStatus() != InspectionStatus.PENDING) {
            throw new BusinessException("Apenas inspeções com status PENDENTE podem ser concluídas.", HttpStatus.BAD_REQUEST);
        }

        inspection.setStatus(InspectionStatus.COMPLETED);
        inspectionRepository.save(inspection);
        return inspectionMapper.toResponseDTO(inspection);
    }

    @Override
    public  InspectionResponseDTO cancelInspection(Long id, Long partnerId) {
        Inspection inspection = getInspectionEntityById(id);

        validatePartnerOwnership(partnerId, inspection);

        if(inspection.getStatus() != InspectionStatus.PENDING) {
            throw new BusinessException("Apenas inspeções com status PENDENTE podem ser canceladas.", HttpStatus.BAD_REQUEST);
        }

        inspection.setStatus(InspectionStatus.CANCELLED);
        inspectionRepository.save(inspection);
        return inspectionMapper.toResponseDTO(inspection);
    }

    private Inspection getInspectionEntityById(Long id) {
        return inspectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Inspeção não encontrada.", HttpStatus.NOT_FOUND));
    }

    private void validatePartnerOwnership(Long partnerId, Inspection inspection) {
        Long ownerId = inspection.getBooking().getVehicle().getPartner().getId();
        if (!ownerId.equals(partnerId)) {
            throw new BusinessException("A inspeção não pertence ao parceiro autenticado.", HttpStatus.FORBIDDEN);
        }
    }
}
