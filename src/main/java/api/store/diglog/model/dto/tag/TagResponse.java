package api.store.diglog.model.dto.tag;

import api.store.diglog.model.entity.Tag;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TagResponse {

    private UUID id;
    private String name;

    @Builder
    public TagResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }
}
