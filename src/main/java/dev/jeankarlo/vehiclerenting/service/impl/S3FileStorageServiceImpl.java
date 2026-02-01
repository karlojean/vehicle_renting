package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.config.S3.BucketType;
import dev.jeankarlo.vehiclerenting.exception.StorageException;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;

@Service
public class S3FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    @Value("${aws.s3.region}")
    private String region;

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
    public String uploadFile(String path, InputStream inputStream, String contentType, long length, BucketType bucketType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketType.getBucketName())
                    .key(path)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, length));
            return path;

        } catch (Exception e) {
            throw new StorageException();
        }
    }

    @Override
    public String generatePresignedUrl(String storagePath, int expirationInMinutes, BucketType bucketType) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketType.getBucketName())
                .key(storagePath)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationInMinutes))
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    @Override
    public String getPublicUrl(String storagePath, BucketType bucketType) {
        if (endpoint != null && !endpoint.isEmpty()) {
            return String.format("%s/%s/%s", endpoint, bucketType.getBucketName(), storagePath);
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketType.getBucketName(),
                Region.of(region).id(),
                storagePath);
    }

    @Override
    public void deleteFile(String storagePath, BucketType bucketType) {
        s3Client.deleteObject(b -> b.bucket(bucketType.getBucketName()).key(storagePath));
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