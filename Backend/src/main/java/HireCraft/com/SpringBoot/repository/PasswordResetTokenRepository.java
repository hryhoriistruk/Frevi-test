package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Find a token by the user and code, ordered by newest first.
    Optional<PasswordResetToken> findFirstByUserEmailAndTokenOrderByExpiresAtDesc(
            String email, String token);

    // delete all expired or used tokens for a user.

    void deleteByUserEmailAndExpiresAtBefore(String email, LocalDateTime cutoff);
}
