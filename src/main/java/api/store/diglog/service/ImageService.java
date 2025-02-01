package api.store.diglog.service;

import api.store.diglog.common.util.S3Util;
import api.store.diglog.model.dto.image.ImageRequest;
import api.store.diglog.model.dto.image.ImageUrlResponse;
import api.store.diglog.model.entity.Image;
import api.store.diglog.model.vo.image.ImagePostVO;
import api.store.diglog.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Util s3Util;

    @Transactional
    public ImageUrlResponse uploadImage(ImageRequest imageRequest) {
        String url = s3Util.uploadImage(imageRequest.getFile());

        return ImageUrlResponse.builder()
                .url(url)
                .build();
    }

    public void savePostImage(ImagePostVO imagePostVO) {
        UUID refId = imagePostVO.getId();

        List<Image> images = imagePostVO.getUrls().stream()
                .map((url) -> Image.builder()
                        .refId(refId)
                        .url(url)
                        .build())
                .toList();

        imageRepository.saveAll(images);
    }

    @Transactional
    public void saveUpdatedPostImage(ImagePostVO imagePostVO) {
        UUID refId = imagePostVO.getId();

        List<Image> postImages = imageRepository.findByRefId(refId);
        List<String> postImageUrls = postImages.stream().map(Image::getUrl).toList();

        List<String> deleteImageUrls = postImageUrls.stream()
                .filter(url -> !imagePostVO.getUrls().contains(url))
                .toList();
        s3Util.deleteImages(deleteImageUrls);
        imageRepository.deleteAllByRefIdAndUrls(refId, deleteImageUrls);

        List<String> notExistUrls = imagePostVO.getUrls().stream()
                .filter(url -> !postImageUrls.contains(url))
                .toList();
        List<Image> images = notExistUrls.stream()
                .map(url -> Image.builder()
                        .refId(refId)
                        .url(url)
                        .build())
                .toList();
        imageRepository.saveAll(images);
    }
}
