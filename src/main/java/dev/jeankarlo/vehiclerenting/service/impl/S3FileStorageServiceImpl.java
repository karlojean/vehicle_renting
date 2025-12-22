package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
public class S3FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3FileStorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file) {

        String keyName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        PutObjectRequest putObj = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putObj, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new BusinessException("Erro ao fazer upload do arquivo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        URL url = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(keyName));

        return url.toString();
    }

    @Override
    public String deleteFile(String fileUrl) {
        // Implementação da exclusão do arquivo no Amazon S3
        return null;
    }

}
