package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.repository.LocationRepository;
import dev.jeankarlo.vehiclerenting.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location getEntityById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Localização com o ID:  " + id + " não encontrada.", HttpStatus.NOT_FOUND));
    }
}
