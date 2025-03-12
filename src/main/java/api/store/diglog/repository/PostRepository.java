package api.store.diglog.repository;

import api.store.diglog.model.entity.Member;
import api.store.diglog.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    Optional<Post> findByIdAndIsDeletedFalse(UUID id);

    Page<Post> findAllByIsDeletedFalse(Pageable pageable);

    Page<Post> findAllByTitleContainingIgnoreCaseAndIsDeletedFalse(String title, Pageable pageable);

    Page<Post> findAllByTagsNameContainingIgnoreCaseAndIsDeletedFalse(String tagName, Pageable pageable);

    Page<Post> findAllByTitleContainingIgnoreCaseOrTagsNameContainingIgnoreCaseAndIsDeletedFalse(String title, String tagName, Pageable pageable);

    Page<Post> findAllByMemberIdAndIsDeletedFalse(UUID memberId, Pageable pageable);

    Page<Post> findAllByMemberIdAndFolderIdInAndIsDeletedFalse(UUID memberId, List<UUID> folderIds, Pageable pageable);

    Page<Post> findAllByMemberUsernameAndTagsIdAndIsDeletedFalse(String username, UUID tagId, Pageable pageable);

    List<Post> findAllByIdInAndMemberId(List<UUID> ids, UUID memberId);

    @Modifying
    @Query("UPDATE Post p SET p.isDeleted = true WHERE p.id = :id AND p.member = :member")
    int updatePostIsDeleted(@Param("id") UUID id, @Param("member") Member member);
}
