package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.booking.BookingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.booking.BookingResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Booking;

import java.util.List;

public interface BookingService {
    BookingResponseDTO create(BookingRequestDTO bookingRequestDTO, Long renterId);
    List<BookingResponseDTO> getBookingsByOwner(Long partnerId);
    List<BookingResponseDTO> getBookingsByRenter(Long renterId);
    void confirmBooking(Long bookingId, Long partnerId);
    void cancelBooking(Long bookingId, Long partnerId);
    Booking getEntityById(Long bookingId);
}

