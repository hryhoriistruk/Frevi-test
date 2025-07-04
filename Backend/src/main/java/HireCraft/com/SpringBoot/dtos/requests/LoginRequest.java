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
public class LoginRequest {
    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;

    public @Email(message = "Must be a valid email address") @NotBlank(message = "Email must not be blank") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Must be a valid email address") @NotBlank(message = "Email must not be blank") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password must not be blank") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password must not be blank") String password) {
        this.password = password;
    }
}

