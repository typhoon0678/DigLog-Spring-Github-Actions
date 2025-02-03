package api.store.diglog.controller;

import api.store.diglog.common.auth.JWTUtil;
import api.store.diglog.model.constant.Role;
import api.store.diglog.model.entity.Member;
import api.store.diglog.model.entity.Post;
import api.store.diglog.model.entity.Tag;
import api.store.diglog.repository.MemberRepository;
import api.store.diglog.repository.PostRepository;
import api.store.diglog.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void beforeEach() {
        memberRepository.save(defaultMember());
        postRepository.save(defaultPost());
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글을 저장에 성공한다.")
    void save() {
        // given
    }

    @Test
    void update() {
    }

    @Test
    void getPost() {
    }

    @Test
    void getPosts() {
    }

    @Test
    void delete() {
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

    private Post defaultPost() {
        Tag tag = Tag.builder()
                .name("tag1")
                .build();

        return Post.builder()
                .title("test title")
                .content("test content")
                .tags(List.of(tag))
                .build();
    }
}