package api.store.diglog.service;

import api.store.diglog.common.util.SecurityUtil;
import api.store.diglog.model.dto.login.LoginRequestDTO;
import api.store.diglog.model.dto.member.MemberUsernameRequestDTO;
import api.store.diglog.model.entity.Member;
import api.store.diglog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member login(LoginRequestDTO loginRequestDTO) {
        Member member = memberRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(); // todo: 에러 구현 (LOGIN_FAILED, 이메일 또는 비밀번호가 일치하지 않습니다.)

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            // todo: 에러 구현 (LOGIN_FAILED, 이메일 또는 비밀번호가 일치하지 않습니다.)
        }

        return member;
    }

    public void updateUsername(MemberUsernameRequestDTO memberUsernameRequestDTO) {
        String email = SecurityUtil.getAuthenticationMemberInfo().getEmail();
        memberRepository.updateUsername(memberUsernameRequestDTO.getUsername(), email);
    }
}
