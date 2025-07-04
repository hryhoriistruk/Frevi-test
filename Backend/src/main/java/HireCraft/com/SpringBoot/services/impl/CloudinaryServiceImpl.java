//package HireCraft.com.SpringBoot.services.impl;
//
//import HireCraft.com.SpringBoot.services.CloudinaryService;
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class CloudinaryServiceImpl implements CloudinaryService {
//    private final Cloudinary cloudinary;
//
//    @Override
//    public String uploadProfileImage(MultipartFile file) {
//        try {
//            @SuppressWarnings("unchecked")
//            Map<String, Object> uploadResult = cloudinary
//                    .uploader()
//                    .upload(file.getBytes(), ObjectUtils.asMap(
//                            "folder", "hirecraft_profiles",
//                            "resource_type", "image",
//                            "transformation", new com.cloudinary.Transformation().width(300).height(300).crop("fill")
//                    ));
//            return (String) uploadResult.get("secure_url");
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload image", e);
//        }
//    }
//}

package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.services.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    private static final Logger log = LoggerFactory.getLogger(CloudinaryServiceImpl.class);

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadProfileImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "hirecraft_profiles",
                    "resource_type", "image",
                    "transformation", new Transformation().width(300).height(300).crop("fill")
            ));
            return uploadResult.get("secure_url").toString();
        }
        catch (IOException | RuntimeException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload profile image", e);
        }
    }

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName, // e.g., "cv_uploads"
                            "resource_type", "auto" // Auto-detect file type (raw for docs, image for images)
                    )
            );
            return uploadResult.get("url").toString(); // Returns the URL of the uploaded file
        } catch (IOException e) {
            // Log the error
            e.printStackTrace();
            throw new IOException("Failed to upload file to Cloudinary", e);
        }
    }
}

