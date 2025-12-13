package dev.jeankarlo.vehiclerenting.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.jeankarlo.vehiclerenting.dto.listing.ListingRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.listing.ListingResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Listing;

@Mapper(componentModel = "spring", uses = { LocationMapper.class })
public interface ListingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Listing toEntity(ListingRequestDTO dto);

    @Mapping(source = "location.state", target = "state")
    @Mapping(source = "location.city", target = "city")
    ListingResponseDTO toResponseDTO(Listing listing);
}
