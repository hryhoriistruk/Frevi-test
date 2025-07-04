package HireCraft.com.SpringBoot;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        String raw = "Admin@123";
        String hash = new BCryptPasswordEncoder().encode(raw);
        System.out.println(hash);
    }
}
