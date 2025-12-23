package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.booking.BookingRequestDTO;
import dev.jeankarlo.vehiclerenting.entity.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingRequestDTO bookingRequestDTO, Long accountId);
    List<Booking> getBookingsByOwner(Long ownerId);
    void confirmBooking(Long bookingId, Long ownerId);
    void cancelBooking(Long bookingId, Long ownerId);
    Booking findEntityById(Long bookingId);
}

