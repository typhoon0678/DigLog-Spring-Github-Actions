package api.store.diglog.model.dto.image;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageUrlResponse {

    private final String url;
}
