package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class S3FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.endpoint}")
    private String endpointUrl;

    public S3FileStorageServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @PostConstruct
    public void init() {
        for (BucketType type : BucketType.values()) {
            createBucketIfNotExists(type);
        }
    }

    @Override
    public String upload(BucketType bucketType, String key, MultipartFile file) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketType.getBucketName())
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            return getUrl(bucketType, key);

        } catch (Exception e) {
            throw new RuntimeException("Falha no upload", e);
        }
    }

    @Override
    public String deleteFile(String fileUrl) {
        return null;
    }

    @Override
    public String getUrl(BucketType bucketType, String key) {

        if(bucketType.isPublic()) {
            return getPublicUrl(bucketType.getBucketName(), key);
        }

        return generateTemporaryURL(bucketType, key);
    }

    private String generateTemporaryURL(BucketType bucketType, String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(r -> r.bucket(bucketType.getBucketName()).key(key))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private String getPublicUrl(String bucket, String key) {
        String baseUrl = endpointUrl.endsWith("/")
                ? endpointUrl.substring(0, endpointUrl.length() - 1)
                : endpointUrl;

        String cleanKey = key.startsWith("/") ? key.substring(1) : key;

        return String.format("%s/%s/%s", baseUrl, bucket, cleanKey);
    }

    private void createBucketIfNotExists(BucketType bucketType) {
        try {
            s3Client.headBucket(r -> r.bucket(bucketType.getBucketName()));
        } catch (NoSuchBucketException e) {
            createBucket(bucketType);
        }
    }

    private void createBucket(BucketType bucketType) {
        try {
            s3Client.createBucket(r -> r.bucket(bucketType.getBucketName()));

            if (bucketType.isPublic()) {
                String policy = """
                        {
                            "Version": "2012-10-17",
                            "Statement": [{
                                "Sid": "PublicReadGetObject",
                                "Effect": "Allow",
                                "Principal": "*",
                                "Action": "s3:GetObject",
                                "Resource": "arn:aws:s3:::%s/*"
                            }]
                        }
                        """.formatted(bucketType.getBucketName());

                s3Client.putBucketPolicy(r -> r.bucket(bucketType.getBucketName()).policy(policy));
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao criar bucket: " + bucketType.getBucketName(), e);
        }
    }
}