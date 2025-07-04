package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.*;
import HireCraft.com.SpringBoot.dtos.response.ChangePasswordResponse;
import HireCraft.com.SpringBoot.dtos.response.ForgotPasswordResponse;
import HireCraft.com.SpringBoot.dtos.response.LoginResponse;
import HireCraft.com.SpringBoot.dtos.response.RegisterResponse;
import HireCraft.com.SpringBoot.dtos.response.ResetPasswordResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    ForgotPasswordResponse forgotPassword(ForgetPasswordRequest request);

    ResetPasswordResponse resetPassword(ResetPasswordRequest request);

    ChangePasswordResponse changePassword(ChangePasswordRequest request, UserDetails userDetails);
}
