package api.store.diglog.model.dto.login;

import api.store.diglog.model.constant.Platform;

public interface OAuth2ResponseDTO {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();

    Platform getPlatform();
}
