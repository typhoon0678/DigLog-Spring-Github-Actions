package api.store.diglog.model.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutRequest {

    private final String email;
}
