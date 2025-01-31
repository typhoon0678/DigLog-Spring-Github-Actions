package api.store.diglog.model.dto.login;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}
