package dev.jeankarlo.vehiclerenting.controller;

import dev.jeankarlo.vehiclerenting.dto.booking.BookingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.booking.BookingResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;


@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> booking(
            @RequestBody BookingRequestDTO bookingRequestDTO,
            @AuthenticationPrincipal Account account
    ){
        Long accountId = account.getId();
        return ResponseEntity.ok(bookingService.create(bookingRequestDTO, accountId));
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<List<BookingResponseDTO>> getReceivedBookings(
            @AuthenticationPrincipal Account account
    ){
        Long ownerId = account.getId();
        return ResponseEntity.ok(bookingService.getBookingsByOwner(ownerId));
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<List<BookingResponseDTO>> getRequestsBooking(
            @AuthenticationPrincipal Account account
    ){
        Long accountId  = account.getId();
        return ResponseEntity.ok(bookingService.getBookingsByRenter(accountId));
    }

    @PatchMapping("/{bookingId}/confirm" )
    public ResponseEntity<Void> confirmBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal Account account
    ){
        Long ownerId = account.getId();
        bookingService.confirmBooking(bookingId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/cancel" )
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal Account account
    ){
        Long ownerId = account.getId();
        bookingService.cancelBooking(bookingId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
