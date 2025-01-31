package api.store.diglog.repository;

import api.store.diglog.model.entity.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RefreshRepository extends JpaRepository<Refresh, UUID> {

    void deleteAllByEmail(String email);

    @Query("SELECT COUNT(r) from Refresh r WHERE r.jwt = :refreshToken")
    int countByRefreshToken(String refreshToken);
}
