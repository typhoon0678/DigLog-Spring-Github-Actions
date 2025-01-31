package api.store.diglog.model.dto.post;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PostRequest {

    private String title;
    private String content;
    private List<UUID> tagIds;
}
