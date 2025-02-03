package api.store.diglog.controller;

import api.store.diglog.common.auth.JWTUtil;
import api.store.diglog.model.constant.Role;
import api.store.diglog.model.dto.post.PostRequest;
import api.store.diglog.model.dto.post.PostUpdateRequest;
import api.store.diglog.model.entity.Member;
import api.store.diglog.model.entity.Post;
import api.store.diglog.model.entity.Tag;
import api.store.diglog.repository.ImageRepository;
import api.store.diglog.repository.MemberRepository;
import api.store.diglog.repository.PostRepository;
import api.store.diglog.repository.TagRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ImageRepository imageRepository;

    @BeforeEach
    void beforeEach() {
        Member member = memberRepository.save(defaultMember("test@example.com"));
        Tag tag = tagRepository.save(defaultTag("tag1"));
        postRepository.save(defaultPost(member, tag));
    }

    @AfterEach
    void afterEach() {
        imageRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 저장에 성공한다.")
    void save() throws Exception {
        // given
        PostRequest dto = new PostRequest();
        dto.setTitle("title");
        dto.setContent("content");
        dto.setUrls(List.of("url1", "url2"));
        dto.setTagNames(List.of("tag1", "tag2"));

        // when
        MvcResult result = mockMvc.perform(post("/api/post")
                        .header("Authorization", getAuthorization("test@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("게시글 수정에 성공한다.")
    void update() throws Exception {
        // given
        Member member = memberRepository.save(defaultMember("test2@example.com"));
        Tag tag = tagRepository.save(defaultTag("tag"));
        Post post = postRepository.save(defaultPost(member, tag));
        PostUpdateRequest dto = new PostUpdateRequest();
        dto.setId(post.getId());
        dto.setTitle("update title");
        dto.setContent("update content");
        dto.setUrls(List.of("url2", "url3"));
        dto.setTagNames(List.of("tag2", "tag3"));

        // when
        MvcResult result = mockMvc.perform(patch("/api/post")
                        .header("Authorization", getAuthorization("test2@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("다른 사용자의 게시글 수정에 실패한다.")
    void update2() throws Exception {
        // given
        Member member = memberRepository.save(defaultMember("test2@example.com"));
        Tag tag = tagRepository.save(defaultTag("tag"));
        Post post = postRepository.save(defaultPost(member, tag));
        PostUpdateRequest dto = new PostUpdateRequest();
        dto.setId(post.getId());
        dto.setTitle("update title");
        dto.setContent("update content");
        dto.setUrls(List.of("url2", "url3"));
        dto.setTagNames(List.of("tag2", "tag3"));

        // when
        MvcResult result = mockMvc.perform(patch("/api/post")
                        .header("Authorization", getAuthorization("test@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    @DisplayName("특정 id의 게시글 조회에 성공한다.")
    void getPost() throws Exception {
        // given
        Member member = memberRepository.save(defaultMember("test2@example.com"));
        Tag tag = tagRepository.save(defaultTag("tag"));
        Post post = postRepository.save(defaultPost(member, tag));
        String id = post.getId().toString();

        // when
        MvcResult result = mockMvc.perform(get("/api/post/" + id)).andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(data.get("title").asText()).isEqualTo("test title");
        assertThat(data.get("content").asText()).isEqualTo("test content");
    }

    @Test
    @DisplayName("해당 id를 가진 게시글이 없는 경우 실패한다.")
    void getPost2() throws Exception {
        // given
        String id = UUID.randomUUID().toString();

        // when
        MvcResult result = mockMvc.perform(get("/api/post/" + id)).andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("검색 조건을 포함한 검색에 성공한다.")
    void getPosts() throws Exception {
        // given
        String sorts = "sorts=createdAt";
        String page = "page=0";
        String size = "size=5";
        String isDescending = "isDescending=true";
        String parameter = "?" + sorts + "&" + page + "&" + size + "&" + isDescending;

        // when
        MvcResult result = mockMvc.perform(get("/api/post" + parameter)).andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(data.get("content").get(0).get("title").asText()).isEqualTo("test title");
        assertThat(data.get("content").get(0).get("content").asText()).isEqualTo("test content");
        assertThat(data.get("content").get(0).get("tags").get(0).get("name").asText()).isEqualTo("tag1");
        assertThat(data.get("page").get("size").asInt()).isEqualTo(5);
    }

    @Test
    @DisplayName("올바르지 않은 검색조건으로 검색 시 실패한다.")
    void getPosts2() throws Exception {
        // given
        String sorts = "sorts=created_at";
        String page = "page=0";
        String size = "size=5";
        String isDescending = "isDescending=true";
        String parameter = "?" + sorts + "&" + page + "&" + size + "&" + isDescending;

        // when
        MvcResult result = mockMvc.perform(get("/api/post" + parameter)).andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("태그로 게시글 검색에 성공한다.")
    void getPostsTag() throws Exception {
        String tagName = "tagName=tag1";
        String page = "page=0";
        String size = "size=5";
        String parameter = "?" + tagName + "&" + page + "&" + size;

        // when
        MvcResult result = mockMvc.perform(get("/api/post/tag" + parameter)).andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(data.get("content").get(0).get("title").asText()).isEqualTo("test title");
        assertThat(data.get("content").get(0).get("content").asText()).isEqualTo("test content");
        assertThat(data.get("content").get(0).get("tags").get(0).get("name").asText()).isEqualTo("tag1");
        assertThat(data.get("page").get("size").asInt()).isEqualTo(5);
    }

    @Test
    @DisplayName("태그를 대문자로 검색했을 때 게시글 검색에 성공한다.")
    void getPostsTag2() throws Exception {
        String tagName = "tagName=TAG1";
        String page = "page=0";
        String size = "size=5";
        String parameter = "?" + tagName + "&" + page + "&" + size;

        // when
        MvcResult result = mockMvc.perform(get("/api/post/tag" + parameter)).andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(data.get("content").get(0).get("title").asText()).isEqualTo("test title");
        assertThat(data.get("content").get(0).get("content").asText()).isEqualTo("test content");
        assertThat(data.get("content").get(0).get("tags").get(0).get("name").asText()).isEqualTo("tag1");
        assertThat(data.get("page").get("size").asInt()).isEqualTo(5);
    }

    @Test
    @DisplayName("삭제 요청 시 isDeleted = true로 변경한다.")
    void delete() throws Exception {
        // given
        Member member = memberRepository.save(defaultMember("test2@example.com"));
        Tag tag = tagRepository.save(defaultTag("tag"));
        Post post = postRepository.save(defaultPost(member, tag));
        PostUpdateRequest dto = new PostUpdateRequest();

        // when
        MvcResult result = mockMvc.perform(patch("/api/post/delete/" + post.getId().toString())
                        .header("Authorization", getAuthorization("test2@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("다른 사용자가 삭제 요청 시 실패한다.")
    void delete2() throws Exception {
        // given
        Member member = memberRepository.save(defaultMember("test2@example.com"));
        Tag tag = tagRepository.save(defaultTag("tag"));
        Post post = postRepository.save(defaultPost(member, tag));
        PostUpdateRequest dto = new PostUpdateRequest();

        // when
        MvcResult result = mockMvc.perform(patch("/api/post/delete/" + post.getId().toString())
                        .header("Authorization", getAuthorization("test@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        JsonNode data = objectMapper.readTree(response.getContentAsString());

        // then
        assertThat(response.getStatus()).isEqualTo(403);
    }

    private Member defaultMember(String email) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode("qwer1234"))
                .roles(Set.of(Role.ROLE_USER))
                .username(email.split("@")[0])
                .isDeleted(false)
                .build();
    }

    private String getAuthorization(String email) {
        return "Bearer " + jwtUtil.generateAccessToken(defaultMember(email));
    }

    private Post defaultPost(Member member, Tag tag) {
        return Post.builder()
                .member(member)
                .title("test title")
                .content("test content")
                .tags(List.of(tag))
                .build();
    }

    private Tag defaultTag(String name) {
        return Tag.builder()
                .name(name)
                .build();
    }
}