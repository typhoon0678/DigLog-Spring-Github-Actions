package api.store.diglog.model.dto.emailVerification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailVerificationSignupRequest {

    @NotBlank
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private final String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$", message = "영문, 숫자를 포함하여 8-16자리로 작성해주세요.")
    private final String password;

    @NotBlank
    private final String code;
}
