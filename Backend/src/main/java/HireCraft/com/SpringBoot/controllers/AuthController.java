package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.*;
import HireCraft.com.SpringBoot.dtos.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import HireCraft.com.SpringBoot.services.AuthService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        ForgotPasswordResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password") // Using PUT for updating a resource (the user's password)
    @PreAuthorize("isAuthenticated()") // Ensure user is logged in
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails // Injects the currently authenticated user's details
    ) {
        ChangePasswordResponse response = authService.changePassword(request, userDetails);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/forgot-password")
//    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
//            @Valid @RequestBody ForgetPasswordRequest request) {
//        return ResponseEntity.ok(authService.forgotPassword(request));
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<ResetPasswordResponse> resetPassword(
//            @Valid @RequestBody ResetPasswordRequest request) {
//        return ResponseEntity.ok(authService.resetPassword(request));
//    }
}


//{
//        "firstName": "Jane",
//        "lastName": "Smith",
//        "email": "provider@example.com",
//        "password": "SecurePass123!",
//        "phoneNo": "+1987654321",
//        "city": "Los Angeles",
//        "state": "California",
//        "country": "United States of America",
//        "role": "ROLE_SERVICE_PROVIDER",
//        "profession": "Plumber",
//        "bio": "Licensed plumber with 10 years experience",
//        "skills": ["Pipe repair", "Installation", "Maintenance"]
//        }