package api.store.diglog.service;

import api.store.diglog.common.exception.CustomException;
import api.store.diglog.model.dto.comment.CommentListRequest;
import api.store.diglog.model.dto.comment.CommentRequest;
import api.store.diglog.model.dto.comment.CommentResponse;
import api.store.diglog.model.entity.Comment;
import api.store.diglog.model.entity.Member;
import api.store.diglog.model.entity.Post;
import api.store.diglog.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MemberService memberService;

    @InjectMocks
    private CommentService commentService;

    @Nested
    class SaveTest {

        private static final int MAX_DEPTH = 2;

        private static final UUID POST_ID = UUID.randomUUID();
        private static final UUID PARENT_COMMENT_ID = UUID.randomUUID();
        private static final String EMAIL = "test1@example.com";
        private static final String USERNAME = "test1";
        private static final String CONTENT = "test content";

        private static final UUID DELETED_COMMENT_ID = UUID.randomUUID();
        private static final UUID MAX_DEPTH_COMMENT_ID = UUID.randomUUID();


        @ParameterizedTest
        @MethodSource("provideSuccess")
        @DisplayName("댓글 생성에 성공한다.")
        void success(UUID parentCommentId, int depth) {
            // given
            CommentRequest dto = new CommentRequest();
            dto.setPostId(POST_ID);
            dto.setContent(CONTENT);

            Comment parentComment = Comment.builder()
                    .id(parentCommentId)
                    .build();

            when(memberService.getCurrentMember()).thenReturn(Member.builder()
                    .email(EMAIL)
                    .username(USERNAME)
                    .build());
            lenient().when(commentRepository.findByIdAndIsDeletedFalse(null)).thenReturn(Optional.empty());
            lenient().when(commentRepository.findByIdAndIsDeletedFalse(parentCommentId)).thenReturn(Optional.of(parentComment));
            lenient().when(commentRepository.getDepthByParentCommentId(parentCommentId, MAX_DEPTH)).thenReturn(depth);

            // when
            Throwable throwable = catchThrowable(() -> commentService.save(dto));

            // then
            assertThat(throwable).isNull();
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        static Stream<Arguments> provideSuccess() {
            return Stream.of(
                    Arguments.of(null, 0),
                    Arguments.of(PARENT_COMMENT_ID, 1),
                    Arguments.of(PARENT_COMMENT_ID, 2)
            );
        }

        @ParameterizedTest
        @MethodSource("provideFail")
        @DisplayName("댓글 생성에 문제가 있는 경우 에러를 띄운다.")
        void fail(UUID parentCommentId) {
            // given
            CommentRequest dto = new CommentRequest();
            dto.setPostId(POST_ID);
            dto.setContent(CONTENT);
            dto.setParentCommentId(parentCommentId);

            Comment parentComment = Comment.builder()
                    .id(parentCommentId)
                    .build();

            when(memberService.getCurrentMember()).thenReturn(Member.builder()
                    .email(EMAIL)
                    .username(USERNAME)
                    .build());
            lenient().when(commentRepository.findByIdAndIsDeletedFalse(DELETED_COMMENT_ID)).thenReturn(Optional.empty());
            lenient().when(commentRepository.findByIdAndIsDeletedFalse(MAX_DEPTH_COMMENT_ID)).thenReturn(Optional.of(parentComment));
            lenient().when(commentRepository.getDepthByParentCommentId(MAX_DEPTH_COMMENT_ID, MAX_DEPTH)).thenReturn(MAX_DEPTH);

            // when
            Throwable throwable = catchThrowable(() -> commentService.save(dto));

            // then
            assertThat(throwable).isInstanceOf(CustomException.class);
        }

        static Stream<Arguments> provideFail() {
            return Stream.of(
                    Arguments.of(DELETED_COMMENT_ID),
                    Arguments.of(MAX_DEPTH_COMMENT_ID)
            );
        }
    }

    @Nested
    class getCommentsTest {

        private static final UUID POST_ID = UUID.randomUUID();
        private static final int PAGE = 0;
        private static final int SIZE = 5;
        private static final UUID PARENT_COMMENT_ID = UUID.randomUUID();
        private static final String CONTENT = "test content";
        private static final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, Sort.by("createdAt"));
        private static final int SELECT_SIZE = 2;
        private static final String EMAIL = "test1@example.com";
        private static final String USERNAME = "test1";

        @ParameterizedTest
        @MethodSource("provideSuccess")
        @DisplayName("댓글 조회에 성공한다.")
        void success(UUID parentCommentId, Page<Comment> selectResult) {
            // given
            CommentListRequest dto = new CommentListRequest();
            dto.setPostId(POST_ID);
            dto.setPage(PAGE);
            dto.setSize(SIZE);
            dto.setParentCommentId(parentCommentId);

            when(memberService.findMemberById(any(UUID.class))).thenReturn(getMember(EMAIL, USERNAME));
            lenient().when(commentRepository.findByPostIdAndParentCommentIdAndIsDeletedFalse(POST_ID, parentCommentId, PAGEABLE)).thenReturn(selectResult);

            // when
            Page<CommentResponse> response = commentService.getComments(dto);

            // then
            assertThat(response.getContent().size()).isEqualTo(SELECT_SIZE);
            assertThat(response.getContent().getFirst().getContent()).isEqualTo(CONTENT);
            assertThat(response.getContent().getFirst().getTaggedUsername()).isEqualTo(USERNAME);
            assertThat(response.getContent().getFirst().isDeleted()).isEqualTo(false);
        }

        static Stream<Arguments> provideSuccess() {
            return Stream.of(
                    Arguments.of(null, getPageComments(null)),
                    Arguments.of(PARENT_COMMENT_ID, getPageComments(PARENT_COMMENT_ID))
            );
        }

        private static Page<Comment> getPageComments(UUID parentCommentId) {
            return new PageImpl<>(List.of(
                    getComment(getMember(EMAIL, USERNAME), parentCommentId),
                    getComment(getMember(EMAIL, USERNAME), parentCommentId)), PAGEABLE, SELECT_SIZE);
        }

        private static Comment getComment(Member member, UUID parentCommentId) {
            return Comment.builder()
                    .id(UUID.randomUUID())
                    .post(Post.builder().id(POST_ID).build())
                    .member(member)
                    .parentComment(Comment.builder().id(parentCommentId).build())
                    .content(CONTENT)
                    .taggedMember(member)
                    .isDeleted(false)
                    .build();
        }

        private static Member getMember(String email, String username) {
            return Member.builder()
                    .id(UUID.randomUUID())
                    .email(email)
                    .username(username)
                    .build();
        }
    }

    @Nested
    class deleteCommentTest {

        private static final UUID MEMBER_ID = UUID.randomUUID();
        private static final UUID COMMENT_ID = UUID.randomUUID();

        private static final UUID INVALID_MEMBER_ID = UUID.randomUUID();
        private static final UUID INVALID_COMMENT_ID = UUID.randomUUID();

        @Test
        @DisplayName("댓글 삭제에 성공한다.")
        void success() {
            // given
            when(memberService.getCurrentMember()).thenReturn(Member.builder().id(MEMBER_ID).build());
            when(commentRepository.updateIsDeletedByCommentIdAndMemberId(COMMENT_ID, MEMBER_ID)).thenReturn(1);

            // when
            Throwable throwable = catchThrowable(() -> commentService.delete(COMMENT_ID));

            // then
            assertThat(throwable).isNull();
            verify(commentRepository, times(1)).updateIsDeletedByCommentIdAndMemberId(any(UUID.class), any(UUID.class));
        }

        @ParameterizedTest
        @MethodSource("provideFail")
        @DisplayName("댓글 삭제에 실패한다.")
        void fail(UUID commentId, UUID memberId) {
            // given
            when(memberService.getCurrentMember()).thenReturn(Member.builder().id(memberId).build());
            lenient().when(commentRepository.updateIsDeletedByCommentIdAndMemberId(INVALID_COMMENT_ID, MEMBER_ID)).thenReturn(0);
            lenient().when(commentRepository.updateIsDeletedByCommentIdAndMemberId(COMMENT_ID, INVALID_MEMBER_ID)).thenReturn(0);
            lenient().when(commentRepository.updateIsDeletedByCommentIdAndMemberId(INVALID_COMMENT_ID, INVALID_MEMBER_ID)).thenReturn(0);

            // when
            Throwable throwable = catchThrowable(() -> commentService.delete(commentId));

            // then
            assertThat(throwable).isInstanceOf(CustomException.class);
        }

        static Stream<Arguments> provideFail() {
            return Stream.of(
                    Arguments.of(COMMENT_ID, INVALID_MEMBER_ID),
                    Arguments.of(INVALID_COMMENT_ID, MEMBER_ID),
                    Arguments.of(INVALID_COMMENT_ID, INVALID_MEMBER_ID)
            );
        }
    }
}