package api.store.diglog.model.dto.emailVerification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailVerificationRequest {

    private String email;
    private String code;
}
