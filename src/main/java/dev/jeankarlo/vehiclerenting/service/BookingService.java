package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.booking.BookingRequestDTO;
import dev.jeankarlo.vehiclerenting.entity.Booking;

public interface BookingService {
    Booking create(BookingRequestDTO bookingRequestDTO, Long accountId);
}

