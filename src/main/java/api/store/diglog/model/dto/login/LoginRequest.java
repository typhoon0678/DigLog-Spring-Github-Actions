package api.store.diglog.model.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {

    private final String email;
    private final String password;
}
