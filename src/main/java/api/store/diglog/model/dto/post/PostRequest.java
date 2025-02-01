package api.store.diglog.model.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class PostRequest {

    private String title;
    private String content;
    private List<String> tagNames;
    private List<String> urls;
}
