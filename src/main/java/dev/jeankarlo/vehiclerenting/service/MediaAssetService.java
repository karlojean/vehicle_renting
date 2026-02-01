package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.entity.MediaAsset;
import org.springframework.web.multipart.MultipartFile;

public interface MediaAssetService {
    MediaAsset uploadAndCreate(MultipartFile file, BucketType bucketType);
}
