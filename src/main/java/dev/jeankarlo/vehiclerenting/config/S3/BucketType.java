package dev.jeankarlo.vehiclerenting.config.S3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BucketType {
    VEHICLES("vehicle-images", true),
    INSPECTIONS("inspection-images", false);

    private final String bucketName;
    private final boolean isPublic;
}