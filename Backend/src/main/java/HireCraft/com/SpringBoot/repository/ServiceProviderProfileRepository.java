package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceProviderProfileRepository extends JpaRepository<ServiceProviderProfile, Long> {
    Optional<ServiceProviderProfile> findByUserId(Long userId);
    Optional<ServiceProviderProfile> findByUserEmail(String email);
}
