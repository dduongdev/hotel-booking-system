package com.dduongdev.hotel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Value("${storage.local.path:./uploads}")
    private String uploadPath;

    @Value("${storage.local.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created upload directory: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final java.util.Set<String> ALLOWED_TYPES = java.util.Set.of(
        "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @Override
    public String upload(MultipartFile file, String filename) {
        validateFile(file);
        try {
            Path targetPath = Paths.get(uploadPath).resolve(filename);
            Files.copy(file.getInputStream(), targetPath);
            log.info("Saved file to: {}", targetPath.toAbsolutePath());
            return baseUrl + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }
        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Only JPG, PNG, WebP, and GIF images are allowed");
        }
    }

    @Override
    public void delete(String filename) {
        try {
            // Extract filename from URL if full URL is provided
            String actualFilename = extractFilename(filename);
            Path path = Paths.get(uploadPath).resolve(actualFilename);
            Files.deleteIfExists(path);
            log.info("Deleted file: {}", path.toAbsolutePath());
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", filename, e);
        }
    }

    @Override
    public String generateFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

    private String extractFilename(String filenameOrUrl) {
        if (filenameOrUrl == null) return "";
        if (filenameOrUrl.contains("/")) {
            return filenameOrUrl.substring(filenameOrUrl.lastIndexOf("/") + 1);
        }
        return filenameOrUrl;
    }
}
