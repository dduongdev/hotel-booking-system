package com.dduongdev.hotel.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "minio")
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final String bucket;
    private final String publicBaseUrl;

    public MinioStorageService(
            MinioClient minioClient,
            @Value("${storage.minio.bucket:hotel-images}") String bucket,
            @Value("${storage.minio.public-base-url}") String publicBaseUrl) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl;
        initBucket();
    }

    private void initBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created MinIO bucket: {}", bucket);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket: {}", bucket, e);
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
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
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            String url = publicBaseUrl + "/" + bucket + "/" + filename;
            log.info("Uploaded to MinIO: {}", url);
            return url;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO: " + filename, e);
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
    public void delete(String filenameOrUrl) {
        try {
            String objectName = extractObjectName(filenameOrUrl);
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build()
            );
            log.info("Deleted from MinIO: {}", objectName);
        } catch (Exception e) {
            log.warn("Failed to delete from MinIO: {}", filenameOrUrl, e);
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

    private String extractObjectName(String filenameOrUrl) {
        if (filenameOrUrl == null) return "";
        if (filenameOrUrl.contains("/")) {
            String path = filenameOrUrl.substring(filenameOrUrl.indexOf("/", filenameOrUrl.indexOf("//") + 2));
            String cleaned = path.startsWith("/") ? path.substring(1) : path;
            if (cleaned.startsWith(bucket + "/")) {
                return cleaned.substring(bucket.length() + 1);
            }
            return cleaned;
        }
        return filenameOrUrl;
    }
}
