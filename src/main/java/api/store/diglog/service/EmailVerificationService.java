package api.store.diglog.service;

import api.store.diglog.model.constant.Role;
import api.store.diglog.model.dto.emailVerification.EmailVerificationRequestDTO;
import api.store.diglog.model.dto.emailVerification.EmailVerificationSignupRequestDTO;
import api.store.diglog.model.entity.EmailVerification;
import api.store.diglog.model.entity.Member;
import api.store.diglog.repository.EmailVerificationRepository;
import api.store.diglog.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void sendMail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            // todo: 에러 구현 (SIGNUP_MEMBER_EXISTS, 이미 가입된 회원입니다.)
        }

        emailVerificationRepository.deleteAllByEmail(email);

        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .code(code)
                .build();

        createAndSendMail(email, code);

        emailVerificationRepository.save(emailVerification);
    }

    private void createAndSendMail(String email, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[DIGLOG] 회원가입 인증 메일");
            String body = "";
            body += "<h3>" + "진행중인 회원가입 페이지에서 아래의 6자리 코드를 입력해주세요." + "</h3>";
            body += "<h1>" + code + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");

            javaMailSender.send(message);
        } catch (MessagingException e) {
            // todo: 에러 구현 (SIGNUP_MAIL_SEND_FAILED, 메일 발송 중 오류가 발생하였습니다.)
        }
    }

    public void checkCode(EmailVerificationRequestDTO emailVerificationRequestDTO) {
        String email = emailVerificationRequestDTO.getEmail();
        String code = emailVerificationRequestDTO.getCode();

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(); // todo: 에러 구현 (SIGNUP_CODE_NOT_EXISTS, 해당 이메일에 대한 인증 코드가 없습니다.)

        if (!emailVerification.getCode().equals(code)) {
            // todo: 에러 구현 (SIGNUP_CODE_NOT_MATCHED, 인증 코드가 일치하지 않습니다.)
        }

        if (emailVerification.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            // todo: 에러 구현 (SIGNUP_CODE_EXPIRED, 코드 유효기간이 만료되었습니다.)
        }

        emailVerificationRepository.updateVerifiedTrue(email);
    }

    @Transactional
    public void verifyAndSignup(EmailVerificationSignupRequestDTO signupRequestDTO) {
        String email = signupRequestDTO.getEmail();
        String code = signupRequestDTO.getCode();

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(); // todo: 에러 구현 (SIGNUP_CODE_NOT_EXISTS, 해당 이메일에 대한 인증 코드가 없습니다.)

        if (!emailVerification.isVerified()) {
            // todo: 에러 구현 (SIGNUP_CODE_NOT_VERIFIED, 인증되지 않은 코드입니다.)
        }

        if (!emailVerification.getCreatedAt().plusMinutes(20).isAfter(LocalDateTime.now())) {
            // todo: 에러 구현 (SIGNUP_CODE_NOT_VERIFIED, 코드 유효기간이 만료되었습니다.)
        }

        if (!emailVerification.getCode().equals(code)) {
            // todo: 에러 구현 (SIGNUP_CODE_NOT_MATCHED, 인증 코드가 일치하지 않습니다.)
        }

        emailVerificationRepository.deleteAllByEmail(email);

        Member member = Member.builder()
                .email(email)
                .username(email.split("@")[0])
                .password(passwordEncoder.encode(signupRequestDTO.getPassword()))
                .roles(new HashSet<>(Set.of(Role.ROLE_USER)))
                .build();

        memberRepository.save(member);
    }
}
