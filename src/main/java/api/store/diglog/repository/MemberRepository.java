package api.store.diglog.repository;

import api.store.diglog.model.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUsername(String username);

    @Query("SELECT m FROM Member m WHERE LOWER(m.username) LIKE CONCAT('%', :username, '%') AND m.isDeleted = false")
    Optional<Member> findByUsernameAndIsDeletedFalse(String username);

    Page<Member> findAllByUsernameContainingAndIsDeletedFalse(String username, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.username = :username WHERE m.email = :email")
    void updateUsername(String username, String email);

    int countByUsername(String username);

    void deleteAllByEmail(String email);
}
