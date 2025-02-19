package api.store.diglog.controller;

import api.store.diglog.model.dto.comment.CommentRequestDto;
import api.store.diglog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.http.HttpStatusCode;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CommentRequestDto commentRequestDto) {
        commentService.save(commentRequestDto);

        return ResponseEntity.status(HttpStatusCode.CREATED).build();
    }
}
