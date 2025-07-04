package HireCraft.com.SpringBoot.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgetPasswordRequest {
    @Email
    @NotBlank
    private String email;

    public @Email @NotBlank String getEmail() {
        return email;
    }
}
