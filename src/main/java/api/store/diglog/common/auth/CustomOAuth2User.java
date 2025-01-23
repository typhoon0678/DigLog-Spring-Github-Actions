package api.store.diglog.common.auth;

import api.store.diglog.model.dto.member.MemberInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final MemberInfoResponseDTO memberInfoResponseDTO;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return memberInfoResponseDTO.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getName() {
        return memberInfoResponseDTO.getEmail();
    }

    public String getUsername() {
        return memberInfoResponseDTO.getUsername();
    }
}
