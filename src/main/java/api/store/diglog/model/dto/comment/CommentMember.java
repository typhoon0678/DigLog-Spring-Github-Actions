package api.store.diglog.model.dto.comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentMember {

    private final String username;
    private final String profileUrl;
}
