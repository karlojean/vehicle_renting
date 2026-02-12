package dev.jeankarlo.vehiclerenting.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionPatchDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Inspection;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.mapper.InspectionMapper;
import dev.jeankarlo.vehiclerenting.repository.InspectionRepository;
import dev.jeankarlo.vehiclerenting.service.BookingService;
import dev.jeankarlo.vehiclerenting.service.InspectionService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import jakarta.transaction.Transactional;

@Service
public class InspectionServiceImpl implements InspectionService {

    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final InspectionRepository inspectionRepository;
    private final InspectionMapper inspectionMapper;

    public InspectionServiceImpl(BookingService bookingService, VehicleService vehicleService,
            InspectionRepository inspectionRepository, InspectionMapper inspectionMapper) {
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.inspectionRepository = inspectionRepository;
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
                InspectionStatus.CANCELLED);

        if (existsInspectionOfType) {
            throw new BusinessException(
                    "Já existe uma inspeção do tipo " + inspectionInitDTO.type() + " para esta reserva.",
                    HttpStatus.BAD_REQUEST);
        }

        if (inspectionInitDTO.type().equals(InspectionType.PICK_UP) && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException(
                    "A abertura de inspeção para retirada só pode ser feita em reservas confirmadas.",
                    HttpStatus.BAD_REQUEST);
        }

        if (inspectionInitDTO.type().equals(InspectionType.DROP_OFF) && booking.getStatus() != BookingStatus.ACTIVE) {
            throw new BusinessException("A abertura de inspeção para devolução só pode ser feita em reservas ativas.",
                    HttpStatus.BAD_REQUEST);
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
    @Transactional
    public InspectionResponseDTO updateById(Long id, InspectionPatchDTO inspectionPatchDTO, Long partnerId) {
        Inspection inspection = getInspectionEntityById(id);

        validatePartnerOwnership(partnerId, inspection);

        if (inspection.getStatus() != InspectionStatus.PENDING) {
            throw new BusinessException("Apenas inspeções com status PENDENTE podem ser atualizadas.",
                    HttpStatus.BAD_REQUEST);
        }

        inspectionMapper.updateInspection(inspection, inspectionPatchDTO);

        inspectionRepository.save(inspection);
        return inspectionMapper.toResponseDTO(inspection);
    }

    @Override
    @Transactional
    public InspectionResponseDTO completeInspection(Long id, Long partnerId) {
        Inspection inspection = getInspectionEntityById(id);

        validatePartnerOwnership(partnerId, inspection);

        if (inspection.getStatus() != InspectionStatus.PENDING) {
            throw new BusinessException("Apenas inspeções com status PENDENTE podem ser concluídas.",
                    HttpStatus.BAD_REQUEST);
        }

        inspection.setStatus(InspectionStatus.COMPLETED);
        inspectionRepository.save(inspection);

        transitionBookingStatus(inspection);

        return inspectionMapper.toResponseDTO(inspection);
    }

    private void transitionBookingStatus(Inspection inspection) {
        Booking booking = inspection.getBooking();

        if (inspection.getType() == InspectionType.PICK_UP && booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.ACTIVE);
        }

        if (inspection.getType() == InspectionType.DROP_OFF && booking.getStatus() == BookingStatus.ACTIVE) {
            booking.setStatus(BookingStatus.COMPLETED);
        }
    }

    @Override
    @Transactional
    public InspectionResponseDTO cancelInspection(Long id, Long partnerId) {
        Inspection inspection = getInspectionEntityById(id);

        validatePartnerOwnership(partnerId, inspection);

        if (inspection.getStatus() != InspectionStatus.PENDING) {
            throw new BusinessException("Apenas inspeções com status PENDENTE podem ser canceladas.",
                    HttpStatus.BAD_REQUEST);
        }

        inspection.setStatus(InspectionStatus.CANCELLED);
        inspectionRepository.save(inspection);
        return inspectionMapper.toResponseDTO(inspection);
    }

    @Override
    public List<InspectionResponseDTO> getInspectionsByBookingId(Long bookingId, Long partnerId) {
        Booking booking = bookingService.getEntityById(bookingId);

        vehicleService.findVehicleByOwnerOrThrow(booking.getVehicle().getId(), partnerId);

        List<Inspection> inspections = inspectionRepository.findByBooking(booking);

        return inspections.stream()
                .map(inspectionMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Inspection getInspectionEntityById(Long id) {
        return inspectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Inspeção não encontrada.", HttpStatus.NOT_FOUND));
    }

    @Override
    public void validatePartnerOwnership(Long partnerId, Inspection inspection) {
        Long ownerId = inspection.getBooking().getVehicle().getPartner().getId();
        if (!ownerId.equals(partnerId)) {
            throw new BusinessException("A inspeção não pertence ao parceiro autenticado.", HttpStatus.FORBIDDEN);
        }
    }
}
