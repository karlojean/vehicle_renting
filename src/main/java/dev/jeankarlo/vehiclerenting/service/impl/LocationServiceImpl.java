package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.location.LocationRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.location.LocationResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.mapper.LocationMapper;
import dev.jeankarlo.vehiclerenting.repository.LocationRepository;
import dev.jeankarlo.vehiclerenting.service.AccountService;
import dev.jeankarlo.vehiclerenting.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final AccountService accountService;

    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper, AccountService accountService) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.accountService = accountService;
    }

    @Override
    public LocationResponseDTO create(LocationRequestDTO locationRequestDTO, Long partnerId) {
        Account partner = accountService.getEntityById(partnerId);

        if (partner.getRole() != AccountRole.PARTNER) {
            throw new BusinessException("Apenas parceiros podem criar localizações.", HttpStatus.FORBIDDEN);
        }

        Location location = locationMapper.toEntity(locationRequestDTO);
        location.setPartner(partner);

        Location savedLocation = locationRepository.save(location);
        return locationMapper.toResponseDTO(savedLocation);
    }

    @Override
    public List<LocationResponseDTO> getAllLocationsByPartnerId(Long partnerId) {
        List<Location> locations = locationRepository.findAllByPartner_Id(partnerId);

        return locations.stream()
                .map(locationMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Location getEntityById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Localização com o ID:  " + id + " não encontrada.", HttpStatus.NOT_FOUND));
    }
}
