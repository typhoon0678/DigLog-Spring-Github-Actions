package api.store.diglog.service;

import api.store.diglog.common.exception.CustomException;
import api.store.diglog.common.util.SecurityUtil;
import api.store.diglog.model.dto.comment.CommentMember;
import api.store.diglog.model.dto.login.LoginRequest;
import api.store.diglog.model.dto.member.MemberProfileResponse;
import api.store.diglog.model.dto.member.MemberUsernameRequest;
import api.store.diglog.model.entity.Member;
import api.store.diglog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static api.store.diglog.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 현재 api 요청을 보낸 Member
    public Member getCurrentMember() {
        String email = SecurityUtil.getAuthenticationMemberInfo().getEmail();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_EMAIL_NOT_FOUND));
    }

    public Member login(LoginRequest loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(LOGIN_FAILED));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CustomException(LOGIN_FAILED);
        }

        return member;
    }

    public void updateUsername(MemberUsernameRequest memberUsernameRequest) {
        String email = SecurityUtil.getAuthenticationMemberInfo().getEmail();
        memberRepository.updateUsername(memberUsernameRequest.getUsername(), email);
    }

    public MemberProfileResponse getProfile() {
        String email = SecurityUtil.getAuthenticationMemberInfo().getEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(LOGIN_FAILED));

        return MemberProfileResponse.builder()
                .email(email)
                .username(member.getUsername())
                .build();
    }

    public CommentMember getCommentMember(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_ID_NOT_FOUND));

        return CommentMember.builder()
                .username(member.getUsername())
                .build();
    }
}
