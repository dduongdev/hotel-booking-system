package com.dduongdev.hotel.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Upload a file and return the URL to access it.
     */
    String upload(MultipartFile file, String filename);

    /**
     * Delete a file by its stored filename/path.
     */
    void delete(String filename);

    /**
     * Generate a unique filename from the original filename.
     */
    String generateFilename(String originalFilename);
}
