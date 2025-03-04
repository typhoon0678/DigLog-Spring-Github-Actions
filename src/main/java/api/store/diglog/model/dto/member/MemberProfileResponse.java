package api.store.diglog.model.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileResponse {

    private final String email;
    private final String username;
    private final String profileUrl;
}
