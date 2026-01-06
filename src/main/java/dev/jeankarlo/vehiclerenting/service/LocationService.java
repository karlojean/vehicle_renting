package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.location.LocationRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.location.LocationResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Location;

import java.util.List;

public interface LocationService {
    LocationResponseDTO create(LocationRequestDTO locationRequestDTO, Long partnerId);
    List<LocationResponseDTO> getAllLocationsByPartnerId(Long partnerId);
    Location getEntityById(Long id);
}
