package api.store.diglog.model.dto.emailVerification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailVerificationRequest {

    private final String email;
    private final String code;
}
