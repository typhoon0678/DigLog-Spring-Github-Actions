package api.store.diglog.model.dto.post;

import api.store.diglog.model.entity.Post;
import api.store.diglog.model.entity.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PostUpdateRequest {

    private final UUID id;
    private final String title;
    private final String content;
    private final List<String> tagNames;
    private final List<String> urls;

    public Post toPost(Post currentPost, List<Tag> tags) {
        return Post.builder()
                .id(id)
                .member(currentPost.getMember())
                .title(title)
                .content(content)
                .tags(tags)
                .createdAt(currentPost.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
