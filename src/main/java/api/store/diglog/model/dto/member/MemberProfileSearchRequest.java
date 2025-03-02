package api.store.diglog.model.dto.member;

import lombok.Data;

@Data
public class MemberProfileSearchRequest {

    private String username;
    private int page;
    private int size;
}
