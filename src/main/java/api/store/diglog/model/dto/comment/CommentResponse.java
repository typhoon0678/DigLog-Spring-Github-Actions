package api.store.diglog.model.dto.comment;

import api.store.diglog.model.dto.member.MemberProfileResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentResponse {

    private final UUID id;
    private final MemberProfileResponse member;
    private final String content;
    private final boolean isDeleted;
    private final LocalDateTime createdAt;
}
