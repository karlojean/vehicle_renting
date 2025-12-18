package dev.jeankarlo.vehiclerenting.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(MultipartFile file);
    String deleteFile(String fileUrl);
}
