package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.listing.ListingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.listing.ListingResponseDTO;

public interface ListingService {
    ListingResponseDTO create(ListingRequestDTO listingRequestDTO);
}
