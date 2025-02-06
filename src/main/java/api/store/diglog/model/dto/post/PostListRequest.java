package api.store.diglog.model.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class PostListRequest {

    private List<String> sorts;
    private int page;
    private int size;
    private Boolean isDescending;
}
