package dev.jeankarlo.vehiclerenting.mapper;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.dto.inspection.inspectionMedia.InspectionMediaResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.InspectionMedia;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Mapper(componentModel = "spring")
public abstract class InspectionMediaMapper {

    @Autowired
    protected FileStorageService fileStorageService;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "originalFilename", source = "mediaAsset.originalFilename")
    @Mapping(target = "contentType", source = "mediaAsset.contentType")
    @Mapping(target = "extension", source = "mediaAsset.extension")
    @Mapping(target = "url", source = "inspectionMedia", qualifiedByName = "resolveUrl")
    public abstract InspectionMediaResponseDTO toResponseDTO(InspectionMedia inspectionMedia);


    @Named("resolveUrl")
    protected String resolveUrl(InspectionMedia inspectionMedia) {
        if (inspectionMedia.getMediaAsset() == null) {
            return null;
        }

        var media = inspectionMedia.getMediaAsset();
        var bucketType = BucketType.INSPECTIONS;
        var path = media.getStoragePath();

        if(Objects.equals(media.getAccessLevel(), "PRIVATE")) {
            return fileStorageService.generatePresignedUrl(path, 30, bucketType);
        }

        return fileStorageService.getPublicUrl(path, bucketType);
    }
}
