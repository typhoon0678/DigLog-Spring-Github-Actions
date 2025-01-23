package api.store.diglog.common.auth;

import api.store.diglog.model.constant.Role;
import api.store.diglog.model.dto.login.KakaoResponseDTO;
import api.store.diglog.model.dto.login.OAuth2ResponseDTO;
import api.store.diglog.model.dto.member.MemberInfoResponseDTO;
import api.store.diglog.model.entity.Member;
import api.store.diglog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ResponseDTO oAuth2ResponseDTO;

        switch (registrationId) {
            case "kakao" -> oAuth2ResponseDTO = new KakaoResponseDTO(oAuth2User.getAttributes());
            default -> {
                return null;
            }
        }

        Optional<Member> optionalMember = memberRepository.findByEmail(oAuth2ResponseDTO.getEmail());
        Member member;
        if (optionalMember.isEmpty()) {

            // 회원 정보가 없는 경우 저장
            member = Member.builder()
                    .email(oAuth2ResponseDTO.getEmail())
                    .username(oAuth2ResponseDTO.getName())
                    .password(UUID.randomUUID().toString())
                    .roles(new HashSet<>(Set.of(Role.ROLE_USER)))
                    .platform(oAuth2ResponseDTO.getPlatform())
                    .build();

            memberRepository.save(member);
        } else {

            member = optionalMember.get();

            if (!member.getPlatform().equals(oAuth2ResponseDTO.getPlatform())) {

                // 플랫폼이 같지 않은 경우 -> 중복 회원가입을 막기 위해 throw Exception
                // todo: 에러 구현 (SIGNUP_PLATFORM_DUPLICATED, SERVER로 회원가입 되어있습니다. 다른 로그인 방법으로 시도해주세요.)
            }

            memberRepository.save(member);
        }

        MemberInfoResponseDTO memberInfoResponseDTO = MemberInfoResponseDTO.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .roles(member.getRoles().stream().map(Role::getRole).collect(Collectors.toSet()))
                .build();

        return new CustomOAuth2User(memberInfoResponseDTO);
    }
}
