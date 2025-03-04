package api.store.diglog.model.dto.comment;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CommentListRequest {

    private final UUID postId;
    private final UUID parentCommentId;
    private final int page;
    private final int size;
}
