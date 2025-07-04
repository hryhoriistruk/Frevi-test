package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.UnifiedUserProfileUpdateRequest;
import HireCraft.com.SpringBoot.dtos.response.UserDetailResponse;
import HireCraft.com.SpringBoot.dtos.response.UserListResponse;
import HireCraft.com.SpringBoot.exceptions.InvalidCvFileException;
import HireCraft.com.SpringBoot.exceptions.UserNotFoundException;
import HireCraft.com.SpringBoot.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
//@RequiredArgsConstructor
public class UserController {
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        List<UserListResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
//    @PreAuthorize("hasAuthority('VIEW_USER_PROFILE')")               // ①
    public ResponseEntity<UserDetailResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserDetailResponse profile = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

//    @GetMapping("/view-profile/{id}")
//    @PreAuthorize("hasAuthority('VIEW_USER_PROFILE')")
//    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long id) {
//        UserDetailResponse user = userService.getUserById(id);
//            }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER_ACCOUNT') or hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-profile")
//    @PreAuthorize("hasAuthority('EDIT_USER_PROFILE')")
    public ResponseEntity<UserDetailResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UnifiedUserProfileUpdateRequest request) {
        UserDetailResponse updated = userService.updateUserProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/update-profile-picture")
    @PreAuthorize("hasAuthority('EDIT_USER_PROFILE')")
    public ResponseEntity<Map<String,String>> uploadProfilePicture(
            @AuthenticationPrincipal UserDetails principal,
            @RequestPart("file") MultipartFile file) {

        String url = userService.updateProfilePicture(principal.getUsername(), file);
        return ResponseEntity.ok(Map.of("profilePictureUrl", url));
    }

    @PostMapping("/upload-cv") // Cleaner endpoint, no need for {email} in path
    public ResponseEntity<?> uploadCv(Principal principal, @RequestParam("file") MultipartFile file) {
        try {
            // The service layer will use the principal to find the user and upload the CV
            String cvUrl = userService.uploadCv(principal, file);
            return ResponseEntity.ok(cvUrl); // Return the URL of the uploaded CV
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidCvFileException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) { // Catch any other unexpected errors during upload
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed treturn ResponseEntity.ok(user);\n" +
                    "//o upload CV: " + e.getMessage());
        }
    }

    @GetMapping("/providers/all")
    @PreAuthorize("hasAuthority('VIEW_PROVIDERS')")
    // Consider adding role-based access if only certain roles can browse providers
    // @PreAuthorize("hasAuthority('VIEW_PROVIDERS')") // Example authority
    public ResponseEntity<List<UserDetailResponse>> getAllServiceProviders() {
        List<UserDetailResponse> providers = userService.getAllServiceProviders();
        return ResponseEntity.ok(providers);
    }

//    @GetMapping("/providers/{providerId}")
//    @PreAuthorize("hasAuthority('VIEW_PROVIDER_PROFILE')")
//    public ResponseEntity<UserDetailResponse> getServiceProviderProfile(@PathVariable Long providerId) {
//        try {
//            UserDetailResponse providerProfile = userService.getServiceProviderById(providerId);
//            return ResponseEntity.ok(providerProfile);
//        } catch (UserNotFoundException e) { // Catches both "User not found" and "User is not a Service Provider"
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//    }

    @GetMapping("/providers/{providerId}")
    @PreAuthorize("hasAuthority('VIEW_PROVIDER_PROFILE')")
    public ResponseEntity<?> getServiceProviderProfile(@PathVariable Long providerId) {
        try {
            UserDetailResponse providerProfile = userService.getServiceProviderById(providerId);
            return ResponseEntity.ok(providerProfile); // Returns ResponseEntity<UserDetailResponse>
        } catch (UserNotFoundException e) {
            // Returns ResponseEntity<String>
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Optional: Endpoint for filtered and paginated providers
    @GetMapping("/providers")
    @PreAuthorize("hasAuthority('VIEW_PROVIDERS')")
    public ResponseEntity<Page<UserDetailResponse>> getFilteredServiceProviders(
            @RequestParam(required = false) String occupation,
            @RequestParam(required = false) Set<String> skills, // Using Set for multiple skills
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double minRating,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) { // Default pagination

        Page<UserDetailResponse> providersPage = userService.getFilteredServiceProviders(
                occupation, skills, city, state, country, minRating, pageable
        );
        return ResponseEntity.ok(providersPage);
    }
}
