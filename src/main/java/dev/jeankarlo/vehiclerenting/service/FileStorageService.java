package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {
    String uploadFile(String path, InputStream inputStream, String contentType, long length, BucketType bucketType);
    String generatePresignedUrl(String storagePath, int expirationInMinutes, BucketType bucketType);
    String getPublicUrl(String storagePath, BucketType bucketType);
    void deleteFile(String storagePath, BucketType bucketType);
}
