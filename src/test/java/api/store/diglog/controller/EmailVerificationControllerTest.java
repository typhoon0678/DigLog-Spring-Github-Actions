package api.store.diglog.controller;

import api.store.diglog.model.dto.emailVerification.EmailVerificationRequest;
import api.store.diglog.model.dto.emailVerification.EmailVerificationSignupRequest;
import api.store.diglog.model.entity.EmailVerification;
import api.store.diglog.repository.EmailVerificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EmailVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @BeforeEach
    void beforeEach() {
        EmailVerification emailVerification = EmailVerification.builder()
                .verified(false)
                .email("test@example.com")
                .code("123456")
                .build();

        emailVerificationRepository.save(emailVerification);
    }

    @AfterEach
    void afterEach() {
        emailVerificationRepository.deleteAll();
    }

    @Test
    @DisplayName("verified = false인 인증 코드를 인증하면 verified = true로 변경된다.")
    void checkCode() throws Exception {
        // given
        EmailVerificationRequest dto = EmailVerificationRequest.builder()
                .email("test@example.com")
                .code("123456")
                .build();

        // when
        // then
        setMockMvc("/api/verify/code", dto)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("해당 이메일의 인증코드가 없는 경우에 에러를 띄운다.")
    void checkCode2() throws Exception {
        // given
        EmailVerificationRequest dto = EmailVerificationRequest.builder()
                .email("test222@example.com")
                .code("123456")
                .build();

        // when
        // then
        setMockMvc("/api/verify/code", dto)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("입력 정보가 잘못된 경우 에러를 띄운다.")
    void checkCode3() throws Exception {
        // given
        EmailVerificationRequest dto = EmailVerificationRequest.builder()
                .code("123456")
                .build();

        EmailVerificationRequest dto2 = EmailVerificationRequest.builder()
                .email("test@example.com")
                .build();

        // when
        // then
        setMockMvc("/api/verify/code", dto)
                .andExpect(status().is4xxClientError());
        setMockMvc("/api/verify/code", dto2)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("인증코드가 일치하지 않은 경우에 에러를 띄운다.")
    void checkCode4() throws Exception {
        // given
        EmailVerificationRequest dto = EmailVerificationRequest.builder()
                .code("567890")
                .build();

        // when
        // then
        setMockMvc("/api/verify/code", dto)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("회원가입 정보를 입력하면 회원가입이 완료된다.")
    void verifyAndSignup() throws Exception {
        // given
        emailVerificationRepository.updateVerifiedTrue("test@example.com");

        EmailVerificationSignupRequest dto = new EmailVerificationSignupRequest();
        dto.setEmail("test@example.com");
        dto.setPassword("qwer1234");
        dto.setCode("123456");

        // when
        // then
        setMockMvc("/api/verify/signup", dto)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("입력 정보가 잘못된 경우 에러를 띄운다.")
    void verifyAndSignup2() throws Exception {
        // given
        emailVerificationRepository.updateVerifiedTrue("test@example.com");

        EmailVerificationSignupRequest dto = new EmailVerificationSignupRequest();
        dto.setEmail("test@example.com");
        dto.setPassword("qwer1234");
        dto.setCode("567890");

        // when
        // then
        setMockMvc("/api/verify/signup", dto)
                .andExpect(status().is4xxClientError());
    }

    private ResultActions setMockMvc(String api, Object dto) throws Exception {
        return mockMvc.perform(post(api)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }
}