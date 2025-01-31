package api.store.diglog.repository;

import api.store.diglog.model.entity.EmailVerification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

    Optional<EmailVerification> findByEmail(String email);

    void deleteAllByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE EmailVerification ev SET ev.verified = true WHERE ev.email = :email")
    void updateVerifiedTrue(String email);
}
