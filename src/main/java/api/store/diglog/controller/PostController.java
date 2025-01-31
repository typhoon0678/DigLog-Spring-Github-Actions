package api.store.diglog.controller;

import api.store.diglog.model.dto.post.PostRequest;
import api.store.diglog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> post(@RequestBody PostRequest postRequest) {
        postService.post(postRequest);

        return ResponseEntity.ok().build();
    }
}
