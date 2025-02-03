package api.store.diglog.controller;

import api.store.diglog.common.auth.JWTUtil;
import api.store.diglog.model.constant.Role;
import api.store.diglog.model.dto.member.MemberUsernameRequest;
import api.store.diglog.model.entity.Member;
import api.store.diglog.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;

    @BeforeEach
    void beforeEach() {
        memberRepository.save(defaultMember());
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("username 변경 요청을 성공한다.")
    void updateUsername() throws Exception {
        // given
        MemberUsernameRequest dto = new MemberUsernameRequest();
        dto.setUsername("newUsername");

        // when
        MvcResult result = mockMvc.perform(post("/api/member/username")
                        .header("Authorization", getAuthorization())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        String content = result.getResponse().getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        memberRepository.findByEmail("test@example.com").get().getUsername().equals("newUsername");
    }

    @Test
    @DisplayName("accessToken을 통해 사용자 정보를 받아온다.")
    void getProfile() throws Exception {
        // given
        // when
        MvcResult result = mockMvc.perform(get("/api/member/profile")
                        .header("Authorization", getAuthorization()))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        String content = result.getResponse().getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(content).contains("test@example.com");
        assertThat(content).contains("username");
    }

    private Member defaultMember() {
        return Member.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("qwer1234"))
                .roles(Set.of(Role.ROLE_USER))
                .username("username")
                .isDeleted(false)
                .build();
    }

    private String getAuthorization() {
        return "Bearer " + jwtUtil.generateAccessToken(defaultMember());
    }
}