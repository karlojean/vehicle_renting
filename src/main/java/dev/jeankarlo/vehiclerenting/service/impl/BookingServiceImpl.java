package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.booking.BookingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.booking.BookingResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.exception.InvalidDataRangeException;
import dev.jeankarlo.vehiclerenting.exception.VehicleUnavailableException;
import dev.jeankarlo.vehiclerenting.mapper.BookingMapper;
import dev.jeankarlo.vehiclerenting.repository.BookingRepository;
import dev.jeankarlo.vehiclerenting.service.AccountService;
import dev.jeankarlo.vehiclerenting.service.BookingService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccountService accountService;
    private final VehicleService vehicleService;

    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper, AccountService accountService, VehicleService vehicleService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.accountService = accountService;
        this.vehicleService = vehicleService;
    }

    @Override
    @Transactional
    public BookingResponseDTO create(BookingRequestDTO bookingRequestDTO, Long renterId) {

        Vehicle vehicle = vehicleService.getEntityById(bookingRequestDTO.vehicleId());

        if (bookingRequestDTO.endDate().isBefore(bookingRequestDTO.startDate())) {
            throw new InvalidDataRangeException("A data de término deve ser posterior à data de início.");
        }

        if (bookingRequestDTO.startDate().equals(bookingRequestDTO.endDate())) {
            throw new InvalidDataRangeException("A data de término deve ser diferente da data de início.");
        }

        if(bookingRepository.existsOverlap(
                bookingRequestDTO.vehicleId(),
                bookingRequestDTO.startDate(),
                bookingRequestDTO.endDate())) {
            throw new VehicleUnavailableException();
        }

        Long rentalDays = ChronoUnit.DAYS.between(bookingRequestDTO.startDate(), bookingRequestDTO.endDate());
        Long totalPrice = Math.multiplyExact(vehicle.getPricePerDayCents(), rentalDays);

        Account account = accountService.getEntityById(renterId);

        Booking booking = bookingMapper.toEntity(bookingRequestDTO);
        booking.setStatus(BookingStatus.PENDING);
        booking.setRenter(account);
        booking.setVehicle(vehicle);
        booking.setTotalPriceCents(totalPrice);

        bookingRepository.save(booking);

        return bookingMapper.toResponseDTO(booking);
    }

    @Override
    public List<BookingResponseDTO> getBookingsByOwner(Long partnerId) {
        List<Booking> bookings = bookingRepository.findByVehicle_Partner_Id(partnerId);

        return bookings.stream().map(bookingMapper::toResponseDTO).toList();
    }

    @Override
    public List<BookingResponseDTO> getBookingsByRenter(Long renterId) {
        List<Booking> bookings =  bookingRepository.findByRenter_Id(renterId);

        return bookings.stream().map(bookingMapper::toResponseDTO).toList();
    }

    @Transactional
    public void confirmBooking(Long bookingId, Long partnerId) {
        Booking booking = this.getEntityById(bookingId);

        if (!booking.getVehicle().getPartner().getId().equals(partnerId)) {
            throw new BusinessException("Você não tem permissão para aprovar esta reserva.", HttpStatus.FORBIDDEN);
        }

        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Apenas reservas pendentes podem ser confirmadas.", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long partnerId) {
        Booking booking = this.getEntityById(bookingId);

        if (!booking.getVehicle().getPartner().getId().equals(partnerId)) {
            throw new BusinessException("Você não tem permissão para aprovar esta reserva.", HttpStatus.FORBIDDEN);
        }

        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Apenas reservas pendentes podem ser canceladas.", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public Booking getEntityById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Booking com o ID " + bookingId + " não encontrado.", HttpStatus.NOT_FOUND));
    }
}
