package api.store.diglog.model.dto.tag;

import api.store.diglog.model.entity.Tag;
import lombok.Data;

import java.util.UUID;

@Data
public class TagResponse {

    private UUID id;
    private String name;

    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }
}
