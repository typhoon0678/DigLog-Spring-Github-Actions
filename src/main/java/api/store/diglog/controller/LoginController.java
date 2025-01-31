package api.store.diglog.controller;

import api.store.diglog.common.auth.JWTUtil;
import api.store.diglog.model.constant.Role;
import api.store.diglog.model.dto.login.LoginRequest;
import api.store.diglog.model.dto.login.LogoutRequest;
import api.store.diglog.model.dto.member.MemberInfoResponse;
import api.store.diglog.model.entity.Member;
import api.store.diglog.model.vo.login.TokenVO;
import api.store.diglog.service.MemberService;
import api.store.diglog.service.RefreshService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;
    private final RefreshService refreshService;
    private final JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        Member member = memberService.login(loginRequest);

        String accessToken = jwtUtil.generateAccessToken(member);
        response.addHeader("Authorization", "Bearer " + accessToken);

        Cookie refreshTokenCookie = jwtUtil.generateRefreshCookie(member);
        response.addCookie(refreshTokenCookie);
        refreshService.save(member.getEmail(), refreshTokenCookie.getValue());

        MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                .status(200)
                .email(member.getEmail())
                .username(member.getUsername())
                .roles(member.getRoles().stream().map(Role::getRole).collect(Collectors.toSet()))
                .build();

        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody LogoutRequest logoutRequest,
            HttpServletResponse response) {
        response.addCookie(jwtUtil.generateLogoutCookie());
        refreshService.delete(logoutRequest.getEmail());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refreshToken", defaultValue = "")
            String refreshToken,
            HttpServletResponse response) {
        MemberInfoResponse memberInfoResponse;

        if (!refreshService.isValid(refreshToken)) {
            memberInfoResponse = MemberInfoResponse.builder()
                    .status(401)
                    .build();
            return ResponseEntity.ok().body(memberInfoResponse);
        }

        TokenVO tokenVO = refreshService.getNewToken(refreshToken);

        response.addHeader("Authorization", "Bearer " + tokenVO.getAccessToken());

        if (tokenVO.getRefreshTokenCookie() != null) {
            response.addCookie(tokenVO.getRefreshTokenCookie());
        }

        memberInfoResponse = MemberInfoResponse.builder()
                .status(200)
                .email(tokenVO.getEmail())
                .username(tokenVO.getUsername())
                .roles(tokenVO.getRoles())
                .build();

        return ResponseEntity.ok().body(memberInfoResponse);
    }
}
