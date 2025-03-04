package api.store.diglog.model.dto.image;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class ImageRequest {

    private final MultipartFile file;
}
