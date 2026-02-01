package dev.jeankarlo.vehiclerenting.mapper;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleMedia.VehicleMediaResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.VehicleMedia;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Mapper(componentModel = "spring")
public abstract class VehicleMediaMapper {

    @Autowired
    protected FileStorageService fileStorageService;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "originalFilename", source = "mediaAssets.originalFilename")
    @Mapping(target = "contentType", source = "mediaAssets.contentType")
    @Mapping(target = "extension", source = "mediaAssets.extension")
    @Mapping(target = "url", source = "vehicleMedia", qualifiedByName = "resolveUrl")
    public abstract VehicleMediaResponseDTO toResponseDTO(VehicleMedia vehicleMedia);

    @Named("resolveUrl")
    protected String resolveUrl(VehicleMedia vehicleMedia) {
        if (vehicleMedia.getMediaAssets() == null) {
            return null;
        }

        var media = vehicleMedia.getMediaAssets();
        var bucketType = BucketType.VEHICLES;
        var path = media.getStoragePath();

        if(Objects.equals(media.getAccessLevel(), "PRIVATE")) {
            return fileStorageService.generatePresignedUrl(path, 30, bucketType);
        }

        return fileStorageService.getPublicUrl(path, bucketType);
    }
}
