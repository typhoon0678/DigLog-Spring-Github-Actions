package api.store.diglog.model.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentMember {

    private String username;
    private String profileUrl;
}
