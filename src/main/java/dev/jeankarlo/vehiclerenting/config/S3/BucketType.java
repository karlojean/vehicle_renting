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

    public static BucketType fromBucketName(String bucketName) {
        for (BucketType bucketType : BucketType.values()) {
            if (bucketType.getBucketName().equals(bucketName)) {
                return bucketType;
            }
        }
        throw new IllegalArgumentException("Erro ao encontrar o bucket " + bucketName);
    }
}