package api.store.diglog.service;

import api.store.diglog.common.exception.CustomException;
import api.store.diglog.model.dto.comment.CommentRequestDto;
import api.store.diglog.model.entity.Comment;
import api.store.diglog.model.entity.Member;
import api.store.diglog.model.entity.Post;
import api.store.diglog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static api.store.diglog.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;

    public void save(CommentRequestDto commentRequestDto) {
        Member member = memberService.getCurrentMember();
        Post post = Post.builder().id(commentRequestDto.getPostId()).build();
        Comment parentComment = null;

        if (commentRequestDto.getParentCommentId() != null) {
            parentComment = commentRepository.findByIdAndIsDeletedFalse(commentRequestDto.getParentCommentId())
                    .orElseThrow(() -> new CustomException(COMMENT_PARENT_ID_NOT_FOUND));

            int MAX_DEPTH = 3;
            int parentDepth = commentRepository.getDepthByParentCommentId(commentRequestDto.getParentCommentId(), MAX_DEPTH);
            if (parentDepth + 1 >= MAX_DEPTH) {
                throw new CustomException(COMMENT_MAX_DEPTH_EXCEEDED);
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(commentRequestDto.getContent())
                .isDeleted(false)
                .parentComment(parentComment)
                .build();
        commentRepository.save(comment);
    }

}
