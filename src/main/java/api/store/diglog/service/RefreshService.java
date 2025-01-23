package api.store.diglog.service;

import java.util.stream.Collectors;

import api.store.diglog.model.constant.Role;
import api.store.diglog.model.vo.login.TokenVO;
import api.store.diglog.model.vo.member.MemberInfoVO;
import org.springframework.stereotype.Service;

import api.store.diglog.common.auth.JWTUtil;
import api.store.diglog.model.entity.Member;
import api.store.diglog.model.entity.Refresh;
import api.store.diglog.repository.MemberRepository;
import api.store.diglog.repository.RefreshRepository;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public void save(String email, String refreshToken) {
        refreshRepository.deleteAllByEmail(email);

        Refresh refresh = Refresh.builder()
                .email(email)
                .jwt(refreshToken)
                .build();

        refreshRepository.save(refresh);
    }

    public void delete(String email) {
        refreshRepository.deleteAllByEmail(email);
    }

    public boolean isExists(String refreshToken) {
        return refreshRepository.countByRefreshToken(refreshToken) > 0;
    }

    public boolean isValid(String refreshToken) {
        return !jwtUtil.validateToken(refreshToken) || isExists(refreshToken);
    }

    @Transactional
    public TokenVO getNewToken(String refreshToken) {
        MemberInfoVO memberInfoVO = jwtUtil.getMemberInfo(refreshToken);
        Member member = memberRepository.findByEmail(memberInfoVO.getEmail())
                .orElseThrow(); // todo: 에러 구현 (MEMBER_EMAIL_NOT_FOUND, 해당 이메일을 가진 회원이 없습니다.)

        String newAccessToken = jwtUtil.generateAccessToken(member);

        Cookie newRefreshTokenCookie = null;
        if (jwtUtil.shouldRenewRefresh(refreshToken)) {
            newRefreshTokenCookie = jwtUtil.generateRefreshCookie(member);
            save(member.getEmail(), newRefreshTokenCookie.getValue());
        }

        return TokenVO.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .roles(member.getRoles().stream().map(Role::getRole).collect(Collectors.toSet()))
                .accessToken(newAccessToken)
                .refreshTokenCookie(newRefreshTokenCookie)
                .build();
    }
}
