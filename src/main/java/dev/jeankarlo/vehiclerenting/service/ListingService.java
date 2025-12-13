package dev.jeankarlo.vehiclerenting.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dev.jeankarlo.vehiclerenting.dto.listing.ListingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.listing.ListingResponseDTO;

public interface ListingService {
    ListingResponseDTO create(ListingRequestDTO listingRequestDTO, Long ownerId);

    Page<ListingResponseDTO> getAll(Pageable pageable);
}
