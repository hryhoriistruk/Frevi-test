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
public class ResetPasswordRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank @Size(min = 6, max = 6)
    private String token;      // the 6-digit code

    @NotBlank @Size(min = 4)
    private String newPassword;

    public @Email @NotBlank String getEmail() {
        return email;
    }

    public @NotBlank @Size(min = 6, max = 6) String getToken() {
        return token;
    }

    public @NotBlank @Size(min = 4) String getNewPassword() {
        return newPassword;
    }
}
