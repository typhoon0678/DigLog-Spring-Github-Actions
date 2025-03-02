package api.store.diglog.model.dto.post;

import lombok.Data;

import java.util.UUID;

@Data
public class PostListMemberRequest {

    private String username;
    private UUID folderId;
    int page;
    int size;
}
