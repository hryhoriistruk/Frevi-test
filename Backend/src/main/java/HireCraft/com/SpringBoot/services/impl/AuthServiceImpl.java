package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.*;
import HireCraft.com.SpringBoot.dtos.response.ChangePasswordResponse;
import HireCraft.com.SpringBoot.dtos.response.ForgotPasswordResponse;
import HireCraft.com.SpringBoot.dtos.response.ResetPasswordResponse;
import HireCraft.com.SpringBoot.exceptions.InvalidResetTokenException;
import HireCraft.com.SpringBoot.exceptions.OldPasswordMismatchException;
import HireCraft.com.SpringBoot.exceptions.PasswordMismatchException;
import HireCraft.com.SpringBoot.models.*;
import HireCraft.com.SpringBoot.repository.*;
import HireCraft.com.SpringBoot.services.AuthService;
import HireCraft.com.SpringBoot.dtos.response.LoginResponse;
import HireCraft.com.SpringBoot.dtos.response.RegisterResponse;
import HireCraft.com.SpringBoot.enums.UserStatus;
import HireCraft.com.SpringBoot.security.jwt.JwtTokenProvider;
import HireCraft.com.SpringBoot.utils.PasswordUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import HireCraft.com.SpringBoot.exceptions.UserAlreadyExistsException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetTokenRepository tokenRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private static final int TOKEN_LENGTH = 6;
    private static final int TOKEN_EXPIRY_MINUTES = 15;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final JavaMailSender mailSender;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, PasswordResetTokenRepository tokenRepository, ServiceProviderProfileRepository serviceProviderProfileRepository, ClientProfileRepository clientProfileRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepository = tokenRepository;
        this.serviceProviderProfileRepository = serviceProviderProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.mailSender = mailSender;
    }

    @Value("${cloudinary.default-profile-url}")
    private String defaultProfileImageUrl;


    @Override
    public RegisterResponse register(RegisterRequest request) {
        // 1. Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "A user with email '" + request.getEmail() + "' already exists."
            );
        }

        Role userRole = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        // 3. Build User entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                // encode password via util
                .passwordHash(PasswordUtil.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .country(request.getCountry())
                .state(request.getState())
                .city(request.getCity())
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Collections.singleton(userRole))
//                .profilePictureUrl(defaultProfileImageUrl)
                .build();

        // 4. Persist
        User savedUser = userRepository.save(user);

        if ("ROLE_PROVIDER".equalsIgnoreCase(request.getRole())) {
            if (request.getOccupation() == null || request.getOccupation().isBlank()) {
                throw new IllegalArgumentException("Occupation is required for service providers.");
            }
            if(request.getHourlyRate() == null || request.getHourlyRate().isBlank()){
                throw new IllegalArgumentException("Hourly Rate is required for service providers.");
            }
        } else if ("ROLE_CLIENT".equalsIgnoreCase(request.getRole())) {
            if (request.getProfession() == null || request.getProfession().isBlank()) {
                throw new IllegalArgumentException("Profession is required for clients.");
            }
            if (request.getPosition() == null || request.getPosition().isBlank()) {
                throw new IllegalArgumentException("Position is required for clients.");
            }
        }

        // 🎯 If provider, create ServiceProviderProfile
        if ("ROLE_PROVIDER".equals(request.getRole())) {
            ServiceProviderProfile profile = ServiceProviderProfile.builder()
                    .occupation(request.getOccupation())
                    .hourlyRate(request.getHourlyRate())
                    .bio(null)            // Optional, can be updated later
                    .cvUrl(null)          // Optional
                    .averageRating(0.0)   // Initial rating
                    .skills(new HashSet<>()) // Empty set
                    .user(savedUser)
                    .build();

            serviceProviderProfileRepository.save(profile); // Inject this repository
        }

        if ("ROLE_CLIENT".equals(request.getRole())) {
            ClientProfile clientProfile = ClientProfile.builder()
                    .position(request.getPosition())
                    .profession(request.getProfession())
                    .companyName(null)
                    .bio(null)
                    .companyWebsiteUrl(null)
                    .user(savedUser)
                    .build();

            clientProfileRepository.save(clientProfile);
        }


        // 5. Return response
        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .message("Registration successful for user: " + savedUser.getEmail())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getValidityInMilliseconds())
                .build();
    }


    // Replace your createOtpEmailTemplate method with this fixed version:

    private String createOtpEmailTemplate(String firstName, String otpCode, int expiryMinutes) {
        String template = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Password Reset Code</title>
            <style>
                body {
                    font-family: 'Arial', sans-serif;
                    line-height: 1.6;
                    color: #333;
                    background-color: #f4f4f4;
                    margin: 0;
                    padding: 0;
                }
                .container {
                    max-width: 600px;
                    margin: 20px auto;
                    background: #ffffff;
                    border-radius: 10px;
                    box-shadow: 0 0 20px rgba(0,0,0,0.1);
                    overflow: hidden;
                }
                .header {
                    
                    color: white;
                    padding: 30px;
                    text-align: center;
                }
                .navLogoSpan{
                  color: #35D07D;
                }
                .header h1 {
                    margin: 0;
                    font-size: 28px;
                    font-weight: bold;
                }
                .content {
                    padding: 40px 30px;
                    text-align: center;
                }
                .greeting {
                    font-size: 18px;
                    color: #555;
                    margin-bottom: 20px;
                }
                .message {
                    font-size: 16px;
                    color: #666;
                    margin-bottom: 30px;
                    line-height: 1.5;
                }
                .otp-container {
                    background: #f8f9fa;
                    border: 2px dashed #dee2e6;
                    border-radius: 8px;
                    padding: 20px;
                    margin: 30px 0;
                }
                .otp-code {
                    font-size: 36px;
                    font-weight: bold;
                    color: #495057;
                    letter-spacing: 8px;
                    margin: 10px 0;
                    font-family: 'Courier New', monospace;
                }
                .otp-label {
                    font-size: 14px;
                    color: #6c757d;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                    margin-bottom: 10px;
                }
                .expiry-info {
                    background: #fff3cd;
                    border: 1px solid #ffeaa7;
                    border-radius: 5px;
                    padding: 15px;
                    margin: 20px 0;
                    color: #856404;
                }
                .security-note {
                    background: #d1ecf1;
                    border: 1px solid #bee5eb;
                    border-radius: 5px;
                    padding: 15px;
                    margin: 20px 0;
                    color: #0c5460;
                    font-size: 14px;
                }
                .footer {
                    background: #f8f9fa;
                    padding: 20px;
                    text-align: center;
                    border-top: 1px solid #dee2e6;
                }
                .footer p {
                    margin: 5px 0;
                    font-size: 14px;
                    color: #6c757d;
                }
                .brand {
                    color: #35D07D;
                    font-weight: bold;
                    text-decoration: none;
                }
                @media (max-width: 600px) {
                    .container {
                        margin: 10px;
                        border-radius: 0;
                    }
                    .header, .content {
                        padding: 20px;
                    }
                    .otp-code {
                        font-size: 28px;
                        letter-spacing: 4px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Hire<span class="navLogoSpan">Craft</span></h1>
                    <p style="margin: 10px 0 0 0; opacity: 0.9;">Password Reset Request</p>
                </div>
                
                <div class="content">
                    <div class="greeting">
                        Hello %s! 👋
                    </div>
                    
                    <div class="message">
                        We received a request to reset your password. Use the verification code below to continue with your password reset.
                    </div>
                    
                    <div class="otp-container">
                        <div class="otp-label">Your Verification Code</div>
                        <div class="otp-code">%s</div>
                    </div>
                    
                    <div class="expiry-info">
                        ⏰ <strong>Important:</strong> This code will expire in %d minutes for your security.
                    </div>
                    
                    <div class="security-note">
                        🛡️ <strong>Security Tip:</strong> If you didn't request this password reset, please ignore this email. Your account remains secure.
                    </div>
                </div>
                
                <div class="footer">
                    <p><strong><a href="#" class="brand">HireCraft</a></strong></p>
                    <p>Connecting talent with opportunity</p>
                    <p style="font-size: 12px; color: #adb5bd;">
                        This is an automated email. Please do not reply to this message.
                    </p>
                </div>
            </div>
        </body>
        </html>
        """;

        return String.format(template, firstName, otpCode, expiryMinutes);
    }

    // Also replace your createOtpPlainTextTemplate method:
    private String createOtpPlainTextTemplate(String firstName, String otpCode, int expiryMinutes) {
        return String.format(
                "Hello %s,\n\n" +
                        "We received a request to reset your password for your HireCraft account.\n\n" +
                        "Your verification code is: %s\n\n" +
                        "This code will expire in %d minutes.\n\n" +
                        "If you didn't request this password reset, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "The HireCraft Team",
                firstName, otpCode, expiryMinutes
        );
    }

    // Enhanced version with both HTML and plain text (recommended for better compatibility)
    @Override
    public ForgotPasswordResponse forgotPassword(ForgetPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String code = String.format("%0" + TOKEN_LENGTH + "d", RANDOM.nextInt(1_000_000));

            PasswordResetToken prt = PasswordResetToken.builder()
                    .user(user)
                    .token(code)
                    .expiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES))
                    .used(false)
                    .build();
            tokenRepository.save(prt);

            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setTo(user.getEmail());
                helper.setFrom("noreply@hirecraft.com");
                helper.setSubject("🔐 Your HireCraft Password Reset Code");

                // Set both HTML and plain text versions
                String htmlContent = createOtpEmailTemplate(user.getFirstName(), code, TOKEN_EXPIRY_MINUTES);
                String plainTextContent = createOtpPlainTextTemplate(user.getFirstName(), code, TOKEN_EXPIRY_MINUTES);

                helper.setText(plainTextContent, htmlContent); // plain text first, then HTML

                mailSender.send(mimeMessage);

            } catch (MessagingException e) {
                System.err.println("Failed to send password reset email: " + e.getMessage());
            }
        });

        return new ForgotPasswordResponse(
                "If that email is registered, you will receive a reset code shortly."
        );
    }

    @Override
    @Transactional // Ensures token and user update are atomic
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        // 1. Validate reset token
        PasswordResetToken prt = tokenRepository
                .findFirstByUserEmailAndTokenOrderByExpiresAtDesc(
                        request.getEmail(), request.getToken())
                .orElseThrow(() -> new InvalidResetTokenException("Invalid reset code."));

        if (prt.isUsed() || prt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("Reset code expired or already used.");
        }

        // Check if new password meets criteria (already done by @Size in DTO, but can add extra checks here if needed)
        // For example, if new password is same as old password
        User user = prt.getUser();
        if (PasswordUtil.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password.");
        }

        // 2. Mark token used
        prt.setUsed(true);
        tokenRepository.save(prt);

        // 3. Update user password
        user.setPasswordHash(PasswordUtil.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // 4. Return success
        return new ResetPasswordResponse("Password has been reset successfully.");
    }

    @Override
    @Transactional // Ensure password update is atomic
    public ChangePasswordResponse changePassword(ChangePasswordRequest request, UserDetails userDetails) {
        // 1. Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found.")); // Should not happen if @PreAuthorize is used

        // 2. Verify old password
        if (!PasswordUtil.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new OldPasswordMismatchException("Old password does not match.");
        }

        // 3. Check if new password matches confirm new password
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new PasswordMismatchException("New password and confirm new password do not match.");
        }

        // 4. Prevent changing to the same password (optional but recommended)
        if (PasswordUtil.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password.");
        }

        // 5. Update user password
        user.setPasswordHash(PasswordUtil.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // 6. Return success
        return new ChangePasswordResponse("Password changed successfully.");
    }

}
