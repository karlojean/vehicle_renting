package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(BucketType bucketType, String key, MultipartFile file);
    String deleteFile(String fileUrl);
    String getUrl(BucketType bucketType, String key);
}
