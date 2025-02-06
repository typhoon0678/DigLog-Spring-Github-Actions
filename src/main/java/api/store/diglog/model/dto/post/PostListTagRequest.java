package api.store.diglog.model.dto.post;

import lombok.Data;

@Data
public class PostListTagRequest {

    private String tagName;
    private int page;
    private int size;
}
