package dev.jeankarlo.vehiclerenting.service.impl;

import org.springframework.stereotype.Service;

import dev.jeankarlo.vehiclerenting.dto.listing.ListingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.listing.ListingResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Listing;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.mapper.ListingMapper;
import dev.jeankarlo.vehiclerenting.repository.ListingRepository;
import dev.jeankarlo.vehiclerenting.service.ListingService;
import dev.jeankarlo.vehiclerenting.service.VehicleService;

@Service
public class ListingServiceImpl implements ListingService {

  private final VehicleService vehicleService;
  private final ListingMapper listingMapper;
  private final ListingRepository listingRepository;

  public ListingServiceImpl(VehicleService vehicleService, ListingMapper listingMapper,
      ListingRepository listingRepository) {
    this.vehicleService = vehicleService;
    this.listingMapper = listingMapper;
    this.listingRepository = listingRepository;
  }

  @Override
  public ListingResponseDTO create(ListingRequestDTO listingRequestDTO, Long ownerId) {
    Vehicle vehicle = vehicleService.findVehicleByOwnerOrThrow(listingRequestDTO.vehicleId(), ownerId);
    Listing listing = listingMapper.toEntity(listingRequestDTO);
    listing.setVehicle(vehicle);

    return listingMapper.toResponseDTO(listingRepository.save(listing));
  }

}
