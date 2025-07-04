//package HireCraft.com.SpringBoot.services;
//
//import org.springframework.web.multipart.MultipartFile;
//
//public interface CloudinaryService {
//
//    String uploadProfileImage(MultipartFile file);
//
//    public String uploadFile(MultipartFile file, String folderName);
//}
package HireCraft.com.SpringBoot.services;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface CloudinaryService {
    String uploadProfileImage(MultipartFile file) throws IOException; // Add this
    String uploadFile(MultipartFile file, String folderName) throws IOException; // Add this
}