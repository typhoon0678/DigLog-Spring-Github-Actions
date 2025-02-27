package api.store.diglog.model.dto.comment;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentRequest {

    private String content;
    private UUID postId;
    private UUID parentCommentId;
    private String taggedUsername;
}
