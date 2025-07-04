package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
///@AllArgsConstructor
public class UserDetailResponse {
    private Long id;
    private Long providerId;
    private Long clientId;
    private String userRole;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String country;
    private String state;
    private String city;
    private String status;

    private double averageRating;
    private String occupation;
    private String hourlyRate;
    private long jobsDone;
    private String providerBio;
    private Set<String> skills;
    private String cvUrl;

    private String companyName;
    private String position;
    private String profession;
    private String companyWebsiteUrl;
    private String clientBio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String profilePictureUrl;


    public UserDetailResponse(Long id, Long providerId, Long clientId, String userRole, String firstName, String lastName, String email, String phoneNumber, String country, String state, String city, String status, double averageRating, String occupation, String hourlyRate, Long jobsDone, String providerBio, Set<String> skills, String cvUrl, String companyName, String position, String profession, String companyWebsiteUrl, String clientBio, LocalDateTime createdAt, LocalDateTime updatedAt, String profilePictureUrl) {
        this.id = id;
        this.providerId = providerId;
        this.clientId = clientId;
        this.userRole = userRole;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.state = state;
        this.city = city;
        this.status = status;
        this.averageRating = averageRating;
        this.occupation = occupation;
        this.hourlyRate = hourlyRate;
        this.jobsDone = jobsDone;
        this.providerBio = providerBio;
        this.skills = skills;
        this.cvUrl = cvUrl;
        this.companyName = companyName;
        this.position = position;
        this.profession = profession;
        this.companyWebsiteUrl = companyWebsiteUrl;
        this.clientBio = clientBio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.profilePictureUrl = profilePictureUrl;
    }
}

