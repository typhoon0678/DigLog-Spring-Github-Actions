package api.store.diglog.model.dto.post;

import api.store.diglog.model.dto.tag.TagResponse;
import api.store.diglog.model.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class PostResponse {

    private final UUID id;
    private final String title;
    private final String content;
    private final String username;
    private final List<TagResponse> tags;
    private final LocalDateTime createdAt;

    @Builder
    public PostResponse(UUID id, String title, String content, String username, List<TagResponse> tags, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.tags = tags;
        this.createdAt = createdAt;
    }

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.username = post.getMember().getUsername();
        this.tags = post.getTags().stream()
                .map(TagResponse::new)
                .toList();
        this.createdAt = post.getCreatedAt();
    }
}
