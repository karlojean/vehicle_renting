package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.entity.*;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.repository.InspectionImageRepository;
import dev.jeankarlo.vehiclerenting.repository.InspectionRepository;
import dev.jeankarlo.vehiclerenting.service.BookingService;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import dev.jeankarlo.vehiclerenting.service.InspectionService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class InspectionServiceImpl implements InspectionService {

    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final InspectionRepository inspectionRepository;
    private final FileStorageService fileStorageService;
    private final InspectionImageRepository inspectionImageRepository;

    public InspectionServiceImpl(BookingService bookingService, VehicleService vehicleService, InspectionRepository inspectionRepository, FileStorageService fileStorageService, InspectionImageRepository inspectionImageRepository) {
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.inspectionRepository = inspectionRepository;
        this.fileStorageService = fileStorageService;
        this.inspectionImageRepository = inspectionImageRepository;
    }

    @Override
    @Transactional
    public void initInspection(InspectionInitDTO inspectionInitDTO, Long ownerId) {
        Booking booking = bookingService.getEntityById(inspectionInitDTO.bookingId());

        vehicleService.findVehicleByOwnerOrThrow(booking.getVehicle().getId(), ownerId);

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
    }


    @Override
    public void uploadInspectionImage(Long inspectionId,  MultipartFile file, Long ownerId) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new BusinessException("Inspeção não encontrada.", HttpStatus.NOT_FOUND));

        String url =  fileStorageService.uploadFile(file);

        InspectionImage inspectionImage = new InspectionImage();
        inspectionImage.setUrl(url);
        inspectionImage.setInspection(inspection);

        inspectionImageRepository.save(inspectionImage);
    }



}
