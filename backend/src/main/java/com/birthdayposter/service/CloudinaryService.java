package com.birthdayposter.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.birthdayposter.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload an image to Cloudinary.
     *
     * @param file   the multipart file to upload
     * @param folder the Cloudinary folder (e.g., "greet-studio/photos")
     * @return a Map containing "url" and "public_id"
     */
    public Map<String, String> uploadImage(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot upload an empty file.");
        }

        try {
            return uploadImage(file.getBytes(), folder);
        } catch (IOException e) {
            log.error("Failed to read image from MultipartFile", e);
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Upload an image to Cloudinary from byte array.
     *
     * @param imageBytes the byte array of the image
     * @param folder     the Cloudinary folder
     * @return a Map containing "url" and "public_id"
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> uploadImage(byte[] imageBytes, String folder) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageBytes,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    ));

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            log.info("Uploaded image to Cloudinary: folder={}, publicId={}", folder, publicId);

            return Map.of("url", url, "public_id", publicId);
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Delete an image from Cloudinary by its public ID.
     *
     * @param publicId the Cloudinary public ID of the image
     */
    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted image from Cloudinary: publicId={}", publicId);
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: publicId={}", publicId, e);
        }
    }
}
