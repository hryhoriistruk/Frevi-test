package HireCraft.com.SpringBoot.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "First name must not be empty")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    private String lastName;

    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email must not be empty")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    @NotBlank(message = "Enter your country")
    private String country;

    @NotBlank(message = "Enter your state")
    private String state;

    @NotBlank(message = "Enter your city")
    private String city;

    private String phoneNumber;

    private String occupation;

    private String hourlyRate;

    private String role;

    public String profession;

    public String position;
}
