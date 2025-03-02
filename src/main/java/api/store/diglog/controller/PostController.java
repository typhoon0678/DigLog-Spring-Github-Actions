package api.store.diglog.controller;

import api.store.diglog.model.dto.post.*;
import api.store.diglog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody PostRequest postRequest) {
        postService.save(postRequest);

        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<?> update(@RequestBody PostUpdateRequest postUpdateRequest) {
        postService.update(postUpdateRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable UUID id) {
        PostResponse postResponse = postService.getPost(id);

        return ResponseEntity.ok().body(postResponse);
    }

    @GetMapping
    public ResponseEntity<?> getPosts(@ParameterObject @ModelAttribute PostListSearchRequest postListSearchRequest) {
        Page<PostResponse> postResponses = postService.getPosts(postListSearchRequest);

        return ResponseEntity.ok().body(postResponses);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getPostsTag(@ParameterObject @ModelAttribute PostListSearchRequest postListSearchRequest) {
        Page<PostResponse> postResponses = postService.searchPosts(postListSearchRequest);

        return ResponseEntity.ok().body(postResponses);
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        postService.delete(id);

        return ResponseEntity.ok().build();
    }
}
