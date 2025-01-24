package api.store.diglog.controller;

import api.store.diglog.model.dto.member.MemberProfileResponseDTO;
import api.store.diglog.model.dto.member.MemberUsernameRequestDTO;
import api.store.diglog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        MemberProfileResponseDTO memberInfoResponseDTO = memberService.getProfile();

        return ResponseEntity.ok().body(memberInfoResponseDTO);
    }
}
