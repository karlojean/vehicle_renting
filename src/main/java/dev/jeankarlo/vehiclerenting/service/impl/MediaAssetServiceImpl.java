package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.entity.MediaAsset;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.repository.MediaAssetRepository;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import dev.jeankarlo.vehiclerenting.service.MediaAssetService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class MediaAssetServiceImpl implements MediaAssetService {

    private final FileStorageService fileStorageService;
    private final MediaAssetRepository mediaAssetRepository;

    public MediaAssetServiceImpl(FileStorageService fileStorageService, MediaAssetRepository mediaAssetRepository) {
        this.fileStorageService = fileStorageService;
        this.mediaAssetRepository = mediaAssetRepository;
    }

    @Override
    public MediaAsset uploadAndCreate(MultipartFile file, BucketType bucketType) {
        String path = UUID.randomUUID() + "/" + file.getOriginalFilename();

        path = fileStorageService.uploadFile(path, getInputStream(file), file.getContentType(), file.getSize(), bucketType);

        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.setStoragePath(path);
        mediaAsset.setBucketName(bucketType.getBucketName());
        mediaAsset.setOriginalFilename(file.getOriginalFilename());
        mediaAsset.setContentType(file.getContentType());
        mediaAsset.setFileSizeInBytes(file.getSize());
        mediaAsset.setExtension(getExtensionByFilename(file.getOriginalFilename()));


        if(bucketType.isPublic()) {
            mediaAsset.setAccessLevel("PUBLIC");
        } else {
            mediaAsset.setAccessLevel("PRIVATE");
        }

        return mediaAssetRepository.save(mediaAsset);
    }

    @Override
    @Transactional
    public void deleteFromStorageAndRepository(UUID mediaAssetId) {
        MediaAsset mediaAsset = mediaAssetRepository.findById(mediaAssetId)
                .orElseThrow(() -> new BusinessException("Mídia não encontrada.", HttpStatus.NOT_FOUND));

        fileStorageService.deleteFile(mediaAsset.getStoragePath(), BucketType.fromBucketName(mediaAsset.getBucketName()));

        mediaAssetRepository.delete(mediaAsset);
    }

    private InputStream getInputStream(MultipartFile file)  {
        try {
            return file.getInputStream();
        } catch (IOException ioException) {
            throw new BusinessException("Falha ao processar o arquivo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getExtensionByFilename(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }


}
