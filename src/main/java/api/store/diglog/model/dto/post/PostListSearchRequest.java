package api.store.diglog.model.dto.post;

import api.store.diglog.model.constant.SearchOption;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListSearchRequest {

    private final String keyword;
    private final SearchOption option;
    private final List<String> sorts;
    private final int page;
    private final int size;
    private final Boolean isDescending;
}
