package HireCraft.com.SpringBoot.dtos.response;

import lombok.Data;

@Data
public class ClientProfileResponse {
    private String position;
    private String profession;
    private String companyName;
    private String bio;
    private String companyWebsiteUrl;
}
