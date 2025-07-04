package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.UnifiedUserProfileUpdateRequest;
import HireCraft.com.SpringBoot.dtos.response.UserDetailResponse;
import HireCraft.com.SpringBoot.dtos.response.UserListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserListResponse> getAllUsers();
    UserDetailResponse getUserById(Long id);
    Long getUserIdFromPrincipal(Principal principal);
    void deleteUser(Long id);
    UserDetailResponse getUserByEmail(String email);
    UserDetailResponse updateUserProfile(String email, UnifiedUserProfileUpdateRequest request);
    String updateProfilePicture(String email, MultipartFile file);
    String uploadCv(Principal principal, MultipartFile file);

    // --- NEW METHODS FOR PROVIDER FUNCTIONALITY ---

    // Get all service providers (for browse page)
    List<UserDetailResponse> getAllServiceProviders();

    // Get a single service provider's detailed profile by ID
    UserDetailResponse getServiceProviderById(Long providerId);

    // --- Optional: For Filtering and Pagination (more advanced) ---
    // This allows fetching providers with pagination and optional filters
    Page<UserDetailResponse> getFilteredServiceProviders(
            String occupation,
            Set<String> skills,
            String city,
            String state,
            String country,
            Double minRating, // Add if you want to filter by minimum rating
            Pageable pageable // For pagination and sorting
    );

}
