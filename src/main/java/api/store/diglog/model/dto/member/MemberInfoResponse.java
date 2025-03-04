package api.store.diglog.model.dto.member;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class MemberInfoResponse {

    private final int status;
    private final String email;
    private final String username;
    private final Set<String> roles;
}
