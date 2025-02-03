package api.store.diglog.repository;

import api.store.diglog.model.entity.Member;
import api.store.diglog.model.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    Optional<Post> findByIdAndIsDeletedFalse(UUID id);

    Page<Post> findAllByIsDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE LOWER(t.name) = LOWER(:tagName) AND p.isDeleted = false")
    Page<Post> findAllByTagNameAndIsDeletedFalse(String tagName, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.isDeleted = true WHERE p.id = :id AND p.member = :member")
    int updatePostIsDeleted(UUID id, Member member);
}
