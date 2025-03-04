package api.store.diglog.model.dto.post;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PostListMemberRequest {

    private final String username;
    private final UUID folderId;
    private final int page;
    private final int size;
}
