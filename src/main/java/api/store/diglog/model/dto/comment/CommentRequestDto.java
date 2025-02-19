package api.store.diglog.model.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CommentRequestDto {

    private String content;
    private UUID postId;
    private UUID parentCommentId;
}
