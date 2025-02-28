package api.store.diglog.model.dto.comment;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentUpdateRequest {

    private UUID id;
    private String content;
    private String taggedUsername;
}
