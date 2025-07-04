package HireCraft.com.SpringBoot.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {}

    /**
     * Encode raw password using BCrypt.
     * @param rawPassword the plain text password
     * @return the hashed password
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * Verify a raw password against an encoded hash.
     * @param rawPassword the plain text password
     * @param encodedPassword the stored hash
     * @return true if matches
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}
