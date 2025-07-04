package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {
    Optional<ClientProfile> findByUserId(Long userId);

    @Query("SELECT c FROM ClientProfile c WHERE c.user.email = :email")
    Optional<ClientProfile> findByUserEmail(String email);
}
