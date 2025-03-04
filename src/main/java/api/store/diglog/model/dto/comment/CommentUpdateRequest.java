package api.store.diglog.model.dto.comment;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CommentUpdateRequest {

    private final UUID id;
    private final String content;
    private final String taggedUsername;
}
