package api.store.diglog.model.dto.post;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostRequest {

    private final String title;
    private final String content;
    private final List<String> tagNames;
    private final List<String> urls;
}
