package api.store.diglog.controller;

import api.store.diglog.model.dto.emailVerification.EmailVerificationCodeRequestDTO;
import api.store.diglog.model.dto.emailVerification.EmailVerificationRequestDTO;
import api.store.diglog.model.dto.emailVerification.EmailVerificationSignupRequestDTO;
import api.store.diglog.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping
    public ResponseEntity<?> sendMail(@RequestBody EmailVerificationCodeRequestDTO emailVerificationCodeRequestDTO) {
        emailVerificationService.sendMail(emailVerificationCodeRequestDTO.getEmail());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/code")
    public ResponseEntity<?> checkCode(@RequestBody EmailVerificationRequestDTO emailVerificationRequestDTO) {
        emailVerificationService.checkCode(emailVerificationRequestDTO);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> verifyAndSignup(@RequestBody EmailVerificationSignupRequestDTO signupRequestDTO) {
        emailVerificationService.verifyAndSignup(signupRequestDTO);

        return ResponseEntity.ok().build();
    }
}
