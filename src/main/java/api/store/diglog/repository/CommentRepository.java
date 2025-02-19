package api.store.diglog.repository;

import api.store.diglog.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Optional<Comment> findByIdAndIsDeletedFalse(UUID id);

    // parentId의 depth를 재귀로 계산, maxDepth 이상인 경우 탐색을 종료하고 maxDepth를 리턴
    @Query(value = "WITH RECURSIVE CommentTree AS (" +
            "SELECT id, parent_id, 0 AS depth FROM comment " +
            "WHERE id = :parentCommentId " +
            "UNION ALL " +
            "SELECT c.id, c.parent_id, ct.depth + 1 FROM comment c " +
            "INNER JOIN CommentTree ct ON c.id = ct.parent_id " +
            "WHERE ct.depth < :maxDepth) " +
            "SELECT MAX(depth) FROM CommentTree", nativeQuery = true)
    int getDepthByParentCommentId(UUID parentCommentId, int maxDepth);
}
