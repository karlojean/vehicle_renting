package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.booking.BookingRequestDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.exception.InvalidDataRangeException;
import dev.jeankarlo.vehiclerenting.exception.VehicleUnavailableException;
import dev.jeankarlo.vehiclerenting.mapper.BookingMapper;
import dev.jeankarlo.vehiclerenting.repository.BookingRepository;
import dev.jeankarlo.vehiclerenting.service.AccountService;
import dev.jeankarlo.vehiclerenting.service.BookingService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    public Booking create(BookingRequestDTO bookingRequestDTO, Long accountId) {

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

        Account account = accountService.getEntityById(accountId);

        Booking booking = bookingMapper.toEntity(bookingRequestDTO);
        booking.setStatus(BookingStatus.PENDING);
        booking.setRenter(account);
        booking.setVehicle(vehicle);
        booking.setTotalPriceCents(totalPrice);


        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByOwner(Long ownerId) {
        return bookingRepository.findByVehicle_Owner_Id(ownerId);
    }
}
