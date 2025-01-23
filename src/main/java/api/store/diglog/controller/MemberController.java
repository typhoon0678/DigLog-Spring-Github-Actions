package api.store.diglog.controller;

import api.store.diglog.common.util.SecurityUtil;
import api.store.diglog.model.dto.member.MemberUsernameRequestDTO;
import api.store.diglog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/username")
    public ResponseEntity<?> updateUsername(@RequestBody MemberUsernameRequestDTO memberUsernameRequestDTO) {
        memberService.updateUsername(memberUsernameRequestDTO);

        return ResponseEntity.ok().build();
    }
}
