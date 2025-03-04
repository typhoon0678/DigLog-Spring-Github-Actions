package api.store.diglog.model.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileSearchRequest {

    private final String username;
    private final int page;
    private final int size;
}
