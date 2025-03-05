package api.store.diglog.model.dto.post;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostListMemberRequest {

    private String username;
    private UUID folderId;
    private int page;
    private int size;
}
