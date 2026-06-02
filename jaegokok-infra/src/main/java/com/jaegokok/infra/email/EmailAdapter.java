package com.jaegokok.infra.email;

import com.jaegokok.domain.email.EmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendInvitation(String toEmail, String inviteUrl) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("[재고콕] 워크스페이스 초대가 도착했습니다");
        msg.setText(
                "안녕하세요!\n\n" +
                "재고콕 워크스페이스에 초대되었습니다.\n\n" +
                "아래 링크를 클릭하여 가입하세요 (7일 내 유효):\n" +
                inviteUrl + "\n\n" +
                "감사합니다.\n재고콕 팀"
        );
        msg.setFrom("noreply@jaegokok.com");
        mailSender.send(msg);
    }
}
