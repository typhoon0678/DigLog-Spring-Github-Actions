package api.store.diglog.model.dto.member;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class MemberInfoResponse {

    private int status;
    private String email;
    private String username;
    private Set<String> roles;
}
