package api.store.diglog.controller;

import api.store.diglog.common.auth.JWTUtil;
import api.store.diglog.model.constant.Role;
import api.store.diglog.model.dto.comment.CommentRequest;
import api.store.diglog.model.entity.Comment;
import api.store.diglog.model.entity.Member;
import api.store.diglog.model.entity.Post;
import api.store.diglog.repository.CommentRepository;
import api.store.diglog.repository.MemberRepository;
import api.store.diglog.repository.PostRepository;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        Member member1 = memberRepository.save(getDefaultMember("test1@example.com"));
        Member member2 = memberRepository.save(getDefaultMember("test2@example.com"));

        Post post = postRepository.save(getDefaultPost(member1));

        List<Comment> depth0Comments = commentRepository.saveAll(getDefaultComments(member1, post, null));
        List<Comment> depth1Comments = commentRepository.saveAll(getDefaultComments(member1, post, depth0Comments.get(0)));
        List<Comment> depth2Comments = commentRepository.saveAll(getDefaultComments(member1, post, depth1Comments.get(0)));
        List<Comment> deletedDepth0Comments = commentRepository.saveAll(getDefaultComments(member1, post, null, true));
        List<Comment> deletedDepth1Comments = commentRepository.saveAll(getDefaultComments(member1, post, deletedDepth0Comments.get(0)));
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 작성에 성공한다.")
    void save() throws Exception {
        // given
        CommentRequest dto = new CommentRequest();
        dto.setPostId(postRepository.findAll().get(0).getId());
        dto.setContent("test content");

        // when
        MvcResult result = mockMvc.perform(post("/api/comment")
                        .header("Authorization", getAuthorization("test1@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        boolean isPresent = commentRepository.findAll().stream().anyMatch(comment -> comment.getContent().equals(dto.getContent()));
        assertThat(isPresent).isTrue();
    }

    @Test
    @DisplayName("로그인되어 있지 않은 경우 댓글 작성에 실패한다.")
    void save2() throws Exception {
        // given
        CommentRequest dto = new CommentRequest();
        dto.setPostId(postRepository.findAll().get(0).getId());
        dto.setContent("test content");

        // when
        MvcResult result = mockMvc.perform(post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("최대 depth를 초과하는 경우 댓글 작성에 실패한다.")
    void save3() throws Exception {
        // given
        CommentRequest dto = new CommentRequest();
        dto.setPostId(postRepository.findAll().get(0).getId());
        dto.setParentCommentId(postRepository.findAll().getLast().getId());
        dto.setContent("test content");

        // when
        MvcResult result = mockMvc.perform(post("/api/comment")
                        .header("Authorization", getAuthorization("test1@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("특정 게시글에 달린 댓글 조회에 성공한다.")
    void getComments() throws Exception {
        // given
        String postId = "postId=" + postRepository.findAll().get(0).getId();
        String page = "page=0";
        String size = "size=5";
        String parameter = "?" + postId + "&" + page + "&" + size;

        // when
        MvcResult result = mockMvc.perform(get("/api/comment" + parameter)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(data.get("content").get(0).get("member").get("username").asText()).isEqualTo("test1");
        assertThat(data.get("content").get(0).get("content").asText()).isEqualTo("content 0");
        assertThat(data.get("content").get(1).get("content").asText()).isEqualTo("content 1");
    }

    @Test
    @DisplayName("특정 게시글에 달린 하위 댓글 조회에 성공한다.")
    void getComments2() throws Exception {
        // given
        String postId = "postId=" + postRepository.findAll().getLast().getId();
        String page = "page=0";
        String size = "size=5";
        String parentCommentId = "parentCommentId=" + commentRepository.findAll().get(0).getId();
        String parameter = "?" + postId + "&" + parentCommentId + "&" + page + "&" + size;

        // when
        MvcResult result = mockMvc.perform(get("/api/comment" + parameter)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(data.get("content").get(0).get("member").get("username").asText()).isEqualTo("test1");
        assertThat(data.get("content").get(0).get("content").asText()).isEqualTo("content 0");
        assertThat(data.get("content").get(1).get("content").asText()).isEqualTo("content 1");
    }

    @Test
    @DisplayName("isDeleted = true인 댓글은 null이 담긴 댓글로 return된다.")
    void getComments3() throws Exception {
        // given
        String postId = "postId=" + postRepository.findAll().getLast().getId();
        String page = "page=0";
        String size = "size=5";
        String parameter = "?" + postId + "&" + page + "&" + size;

        // when
        MvcResult result = mockMvc.perform(get("/api/comment" + parameter)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(data.get("content").get(2).get("member").asText()).isEqualTo("null");
        assertThat(data.get("content").get(2).get("content").asText()).isEqualTo("null");
        assertThat(data.get("content").get(2).get("deleted").asBoolean()).isTrue();
    }

    @Test
    @DisplayName("삭제된 댓글의 하위 댓글은 조회에 성공한다.")
    void getComments4() throws Exception {
        // given
        String postId = "postId=" + postRepository.findAll().getLast().getId();
        String page = "page=0";
        String size = "size=5";
        String parentCommentId = "parentCommentId=" + commentRepository.findAll().get(2).getId();
        String parameter = "?" + postId + "&" + parentCommentId + "&" + page + "&" + size;

        // when
        MvcResult result = mockMvc.perform(get("/api/comment" + parameter)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(data.get("content").get(0).get("member").get("username").asText()).isEqualTo("test1");
        assertThat(data.get("content").get(0).get("content").asText()).isEqualTo("content 0");
    }

    @Test
    @DisplayName("댓글 삭제에 성공한다.")
    void delete() throws Exception {
        // given
        UUID commentId = commentRepository.findAll().get(0).getId();

        // when
        MvcResult result = mockMvc.perform(patch("/api/comment/delete/" + commentId)
                        .header("Authorization", getAuthorization("test1@example.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        boolean isPresent = commentRepository.findByIdAndIsDeletedFalse(commentId).isPresent();
        assertThat(isPresent).isFalse();
    }

    @Test
    @DisplayName("다른 사용자 댓글 삭제에 실패한다.")
    void delete2() throws Exception {
        // given
        UUID commentId = commentRepository.findAll().get(0).getId();

        // when
        MvcResult result = mockMvc.perform(patch("/api/comment/delete/" + commentId)
                        .header("Authorization", getAuthorization("test2@example.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    private Member getDefaultMember(String email) {
        return Member.builder()
                .email(email)
                .username(email.split("@")[0])
                .password(passwordEncoder.encode("qwer1234"))
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    private Post getDefaultPost(Member member) {
        return Post.builder()
                .title("test title")
                .content("test content")
                .member(member)
                .build();
    }

    private List<Comment> getDefaultComments(Member member, Post post, Comment parentComment, boolean isDeleted) {
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            comments.add(Comment.builder()
                    .post(post)
                    .member(member)
                    .parentComment(parentComment)
                    .content("content " + i)
                    .isDeleted(isDeleted)
                    .build());
        }

        return comments;
    }

    private List<Comment> getDefaultComments(Member member, Post post, Comment parentComment) {
        return getDefaultComments(member, post, parentComment, false);
    }

    private String getAuthorization(String email) {
        return "Bearer " + jwtUtil.generateAccessToken(getDefaultMember(email));
    }
}