package api.store.diglog.model.dto.emailVerification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationCodeRequest {

    @NotBlank
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;
}
