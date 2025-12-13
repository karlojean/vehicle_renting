package dev.jeankarlo.vehiclerenting.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.jeankarlo.vehiclerenting.dto.listing.ListingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.listing.ListingResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.service.ListingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/listings")
public class ListingController {

  private final ListingService listingService;

  public ListingController(ListingService listingService) {
    this.listingService = listingService;
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('RENTING_PARTNER')")
  public ListingResponseDTO listingResponseDTO(
      @RequestBody @Valid ListingRequestDTO listingRequestDTO,
      @AuthenticationPrincipal Account account) {

    Long ownerId = account.getId();
    return listingService.create(listingRequestDTO, ownerId);
  }

  @GetMapping()
  public Page<ListingResponseDTO> getAll(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = Pageable.ofSize(size).withPage(page);
    return listingService.getAll(pageable);
  }

}
