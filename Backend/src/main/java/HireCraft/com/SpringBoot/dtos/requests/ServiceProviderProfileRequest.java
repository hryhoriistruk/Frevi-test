package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;

@Data
public class ServiceProviderProfileRequest {
    private String occupation;
    private String bio;
    private String cvUrl;
}
