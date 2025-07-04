package HireCraft.com.SpringBoot.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "Old password cannot be empty") // Added custom message
    private String oldPassword;

    @NotBlank(message = "New password cannot be empty") // Added custom message
    @Size(min = 8, message = "New password must be at least 8 characters long") // Recommended minimum size for new password
    private String newPassword;

    @NotBlank(message = "Confirm new password cannot be empty") // Added custom message
    @Size(min = 8, message = "Confirm new password must be at least 8 characters long") // Recommended minimum size for confirm new password
    private String confirmNewPassword;

    public @NotBlank(message = "Old password cannot be empty") String getOldPassword() {
        return oldPassword;
    }

    public @NotBlank(message = "New password cannot be empty") @Size(min = 8, message = "New password must be at least 8 characters long") String getNewPassword() {
        return newPassword;
    }

    public @NotBlank(message = "Confirm new password cannot be empty") @Size(min = 8, message = "Confirm new password must be at least 8 characters long") String getConfirmNewPassword() {
        return confirmNewPassword;
    }
}
