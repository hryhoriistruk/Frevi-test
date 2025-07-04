package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.UnifiedUserProfileUpdateRequest;
import HireCraft.com.SpringBoot.dtos.response.ProviderDashboardMetricsResponse;
import HireCraft.com.SpringBoot.enums.RoleName;
import HireCraft.com.SpringBoot.exceptions.InvalidCvFileException;
import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.models.Role;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.BookingService;
import HireCraft.com.SpringBoot.services.CloudinaryService;
import HireCraft.com.SpringBoot.services.UserService;
import HireCraft.com.SpringBoot.dtos.response.UserDetailResponse;
import HireCraft.com.SpringBoot.dtos.response.UserListResponse;
import HireCraft.com.SpringBoot.models.User;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import HireCraft.com.SpringBoot.exceptions.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
///@RequiredArgsConstructor
@Transactional(readOnly = true)
//@Slf4j
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;
    private final BookingService bookingService;

    public UserServiceImpl(UserRepository userRepository, CloudinaryService cloudinaryService, ClientProfileRepository clientProfileRepository, ServiceProviderProfileRepository serviceProviderProfileRepository, BookingService bookingService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.clientProfileRepository = clientProfileRepository;
        this.serviceProviderProfileRepository = serviceProviderProfileRepository;
        this.bookingService = bookingService;
    }

    @Override
    public List<UserListResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserListResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getCity(),
                        user.getState(),
                        user.getCountry(),
                        user.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetailResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));
        return mapToDetail(user);
    }

    @Override
    public Long getUserIdFromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getId();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Cannot delete, user not found with ID " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDetailResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return mapToDetail(user);
    }

    @Override
    @Transactional
    public UserDetailResponse updateUserProfile(String email, UnifiedUserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // === Update base user fields ===
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getCity() != null) user.setCity(request.getCity());

        // === Client Profile Update ===
        if (user.getClientProfile() != null) {
            ClientProfile clientProfile = user.getClientProfile();
            if(request.getPosition() !=null) clientProfile.setPosition(request.getPosition());
            if(request.getProfession() !=null) clientProfile.setProfession(request.getProfession());
            if (request.getCompanyName() != null) clientProfile.setCompanyName(request.getCompanyName());
            if (request.getCompanyWebsiteUrl() != null) clientProfile.setCompanyWebsiteUrl(request.getCompanyWebsiteUrl());
            if (request.getClientBio() != null) clientProfile.setBio(request.getClientBio());
            clientProfileRepository.save(clientProfile);
        }

        // === Service Provider Profile Update ===
        if (user.getServiceProviderProfile() != null) {
            ServiceProviderProfile providerProfile = user.getServiceProviderProfile();
            if(request.getOccupation() !=null) providerProfile.setOccupation(request.getOccupation());
            if(request.getHourlyRate() !=null) providerProfile.setHourlyRate(request.getHourlyRate());
            if (request.getProviderBio() != null) providerProfile.setBio(request.getProviderBio());
            if (request.getSkills() != null && !request.getSkills().isEmpty()) {
                providerProfile.setSkills(request.getSkills());
            }
            serviceProviderProfileRepository.save(providerProfile);
        }

        userRepository.save(user);

//        return userMapper.toUserDetailResponse(user);
        return mapToDetail(user);

    }


    @Override
    @Transactional
    public String updateProfilePicture(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        String url; // Declare url outside try-catch
        try {
            url = cloudinaryService.uploadProfileImage(file); // Call can throw IOException
        } catch (IOException e) {
            log.error("Failed to upload profile picture for user {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to upload profile picture due to a file processing error.", e);
        }

        user.setProfilePictureUrl(url);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return url;
    }

    @Override
    @Transactional
    public String uploadCv(Principal principal, MultipartFile file) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        if (user.getServiceProviderProfile() == null) {
            throw new IllegalArgumentException("User is not a service provider or does not have a provider profile. CV upload is for providers only.");
        }

        if (file.isEmpty()) {
            throw new InvalidCvFileException("Please select a file to upload.");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") &&
                !contentType.equals("application/msword") &&
                !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new InvalidCvFileException("Only PDF and Word documents are allowed for CVs.");
        }

        String cvUrl; // Declare cvUrl outside try-catch
        try {
            String folder = "hirecraft_cvs";
            cvUrl = cloudinaryService.uploadFile(file, folder); // Call can throw IOException

            ServiceProviderProfile providerProfile = user.getServiceProviderProfile();
            providerProfile.setCvUrl(cvUrl);
            serviceProviderProfileRepository.save(providerProfile);

            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("CV uploaded successfully for user {}: {}", email, cvUrl);
            return cvUrl;
        } catch (IOException e) { // Catch the IOException from CloudinaryService methods
            log.error("Failed to upload CV for user {}. File processing error: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to upload CV due to a file processing error.", e);
        } catch (RuntimeException e) {
            // This catch block handles RuntimeExceptions (like those from CloudinaryServiceImpl if converted)
            log.error("Cloudinary service error during CV upload for user {}. Error: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to upload CV to storage service.", e);
        }
    }

    @Override
    public List<UserDetailResponse> getAllServiceProviders() {
        // Find all users that have the ROLE_PROVIDER
        return userRepository.findByRoles_Name(RoleName.ROLE_PROVIDER.name()).stream()
                .map(this::mapToDetail) // Reuse your existing mapping logic
                .collect(Collectors.toList());
    }

    @Override
    public UserDetailResponse getServiceProviderById(Long providerId) { // Parameter name changed for clarity
        // Find the ServiceProviderProfile by its primary key (ID)
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findById(providerId) // Use findById
                .orElseThrow(() -> new UserNotFoundException("Service Provider Profile not found with ID " + providerId));

        User user = providerProfile.getUser(); // Get the associated User

        // Check if the associated user actually has the ROLE_PROVIDER
        boolean isProvider = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleName.ROLE_PROVIDER.name()));

        if (!isProvider) {
            // This case should ideally not be hit if your data is consistent,
            // as we already found a ServiceProviderProfile.
            throw new UserNotFoundException("User associated with Provider Profile ID " + providerId + " is not a Service Provider.");
        }

        return mapToDetail(user);
    }



    // --- Optional: Implementation for Filtering and Pagination ---
    @Override
    public Page<UserDetailResponse> getFilteredServiceProviders(
            String occupation,
            Set<String> skills,
            String city,
            String state,
            String country,
            Double minRating,
            Pageable pageable) {

        // Use Spring Data JPA Specifications for dynamic queries
        Specification<User> spec = Specification.where(null);

        // Filter by ROLE_PROVIDER
        spec = spec.and((root, query, criteriaBuilder) -> {
            // Join with roles to filter by role name
            return criteriaBuilder.equal(root.join("roles").get("name"), RoleName.ROLE_PROVIDER.name());
        });

        // Add filters based on provided criteria
        if (occupation != null && !occupation.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("serviceProviderProfile").get("occupation")), "%" + occupation.toLowerCase() + "%"));
        }
        if (city != null && !city.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
        }
        if (state != null && !state.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("state")), "%" + state.toLowerCase() + "%"));
        }
        if (country != null && !country.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), "%" + country.toLowerCase() + "%"));
        }
        if (minRating != null && minRating >= 0) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("serviceProviderProfile").get("averageRating"), minRating));
        }

        // Filtering by skills is more complex as 'skills' is a Set<String>
        // You might need to use a custom query or a different data structure if you want advanced "contains any of these skills" querying.
        // For a simple "provider has ALL of these skills", it's feasible with 'LIKE' on a comma-separated string,
        // but for 'ANY', it often requires a more sophisticated approach or native query if skills are stored as a single string.
        // If skills are stored in a separate join table, it's easier.
        // Assuming `skills` in ServiceProviderProfile is a single string for now (e.g., "Java,Spring,React")
        // If `skills` is a @ElementCollection Set<String> in JPA, querying it directly can be tricky with Specifications.
        // For a simple solution, let's assume `skills` property in ServiceProviderProfile is a single string.
        // If it's a real Set<String>, you might need to iterate or use a `MemberOf` criteria builder function or a native query.
        if (skills != null && !skills.isEmpty()) {
            // This is a basic approach and might not be efficient for large skill sets or if skills are stored as a collection.
            // It assumes `skills` in `ServiceProviderProfile` is mapped as a string like "skill1,skill2"
            // or that you want to check if the stored skills string contains ANY of the provided skills.
            for (String skill : skills) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("serviceProviderProfile").get("skills")), "%" + skill.toLowerCase() + "%")
                );
            }
            // A more robust solution for Set<String> would involve a custom query or a separate skill entity.
        }


        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserDetailResponse> content = userPage.getContent().stream()
                .map(this::mapToDetail)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, userPage.getTotalElements());
    }

    private UserDetailResponse mapToDetail(User user) {
        // Default values for profile-specific fields
        double averageRating = 0.0;
        Long providerId = null;
        String occupation = null;
        String hourlyRate=null;
        Long jobsDone = null;
        String providerBio = null;
        Set<String> skills = null;
        String cvUrl = null;

        Long clientId = null;
        String companyName = null;
        String position = null;
        String profession = null;
        String companyWebsiteUrl = null;
        String clientBio = null;

        String userRole = "";

        // Assuming User has a getRoles() method that returns a Set<Role>
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            // Find the primary role. You might have specific logic here,
            // e.g., if a user can be both ROLE_CLIENT and ROLE_PROVIDER (unlikely but possible),
            // or if ROLE_ADMIN takes precedence.
            // For simplicity, let's just pick the first applicable role.
            for (Role role : user.getRoles()) {
                if (role.getName().equals(RoleName.ROLE_PROVIDER.name())) {
                    userRole = RoleName.ROLE_PROVIDER.name();
                    break; // Found provider role, exit
                } else if (role.getName().equals(RoleName.ROLE_CLIENT.name())) {
                    userRole = RoleName.ROLE_CLIENT.name();
                    // Don't break if provider role might exist and take precedence
                    // (Depends on your business logic)
                }
                // If you only expect one main role, you could just assign the first one:
                // userRole = role.getName();
                // break;
            }
        }

        if (user.getServiceProviderProfile() != null) {
            ServiceProviderProfile providerProfile = user.getServiceProviderProfile();
            providerId = providerProfile.getId();
            averageRating = providerProfile.getAverageRating();
            occupation = providerProfile.getOccupation();
            hourlyRate = providerProfile.getHourlyRate();
            providerBio = providerProfile.getBio();
            skills = providerProfile.getSkills();
            cvUrl = providerProfile.getCvUrl();
            if (providerId != null) {
                jobsDone = bookingService.countCompletedJobsForProvider(providerId); // <--- Call the method from BookingService
            } else {
                jobsDone = 0L; // Default if providerId is somehow null
            }
        } else if (user.getClientProfile() != null) {
            ClientProfile clientProfile = user.getClientProfile();
            clientId = clientProfile.getId();
            companyName = clientProfile.getCompanyName();
            position = clientProfile.getPosition();
            profession = clientProfile.getProfession();
            companyWebsiteUrl = clientProfile.getCompanyWebsiteUrl();
            clientBio = clientProfile.getBio();
        }

        return new UserDetailResponse(
                user.getId(),
                providerId,
                clientId,
                userRole,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getState(),
                user.getCity(),
                user.getStatus().name(),
                averageRating,
                occupation,
                hourlyRate,
                bookingService.countCompletedJobsForProvider(providerId),
                providerBio,
                skills,
                cvUrl,
                companyName,
                position,
                profession,
                companyWebsiteUrl,
                clientBio,
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfilePictureUrl()

        );
    }
}
