package api.store.diglog.model.dto.emailVerification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailVerificationSignupRequestDTO {

    @NotBlank
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "이메일 형식에 맞게 작성해주세요.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d|.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.{8,16}).*|^(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.{8,}).*$", message = "영문, 숫자, 특수문자 중 2개 이상을 포함하여 8-16자리로 작성해주세요.")
    private String password;

    @NotBlank
    private String code;
}
