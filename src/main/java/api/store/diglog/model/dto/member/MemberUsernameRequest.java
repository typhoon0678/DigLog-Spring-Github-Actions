package api.store.diglog.model.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberUsernameRequest {

    private final String username;
}
