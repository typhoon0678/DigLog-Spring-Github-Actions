package api.store.diglog.model.dto.comment;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentListRequest {

    private UUID postId;
    private UUID parentCommentId;
    private int page;
    private int size;

}
