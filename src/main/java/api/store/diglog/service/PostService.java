package api.store.diglog.service;

import api.store.diglog.model.dto.post.PostRequest;
import api.store.diglog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void post(PostRequest postRequest) {

    }
}
