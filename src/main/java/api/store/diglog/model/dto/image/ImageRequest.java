package api.store.diglog.model.dto.image;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageRequest {

    private MultipartFile file;
}
