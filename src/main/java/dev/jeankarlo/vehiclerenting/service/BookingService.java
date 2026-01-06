package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.booking.BookingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.booking.BookingResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Booking;

import java.util.List;

public interface BookingService {
    BookingResponseDTO create(BookingRequestDTO bookingRequestDTO, Long accountId);
    List<BookingResponseDTO> getBookingsByOwner(Long ownerId);
    List<BookingResponseDTO> getBookingsByRenter(Long requesterId);
    void confirmBooking(Long bookingId, Long ownerId);
    void cancelBooking(Long bookingId, Long ownerId);
    Booking getEntityById(Long bookingId);
}

