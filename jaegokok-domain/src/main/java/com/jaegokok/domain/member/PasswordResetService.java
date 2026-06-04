package com.jaegokok.domain.member;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.domain.email.EmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetService {

    private final MemberRepository memberRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailPort emailPort;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    public void forgotPassword(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        PasswordResetToken token = passwordResetTokenRepository.save(member.id());
        String resetUrl = baseUrl + "/reset-password?token=" + token.token();
        emailPort.sendPasswordReset(member.email(), resetUrl);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVITATION_NOT_FOUND));
        if (prt.used()) throw new CustomException(ErrorCode.INVITATION_ALREADY_USED);
        if (LocalDateTime.now().isAfter(prt.expiresAt())) throw new CustomException(ErrorCode.INVITATION_EXPIRED);
        memberRepository.updatePassword(prt.memberId(), passwordEncoder.encode(newPassword));
        passwordResetTokenRepository.markUsed(prt.id());
    }
}
