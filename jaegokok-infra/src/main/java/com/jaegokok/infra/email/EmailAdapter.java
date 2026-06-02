package com.jaegokok.infra.email;

import com.jaegokok.domain.email.EmailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendInvitation(String toEmail, String inviteUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setFrom("noreply@jaegokok.com", "재고콕");
            helper.setSubject("[재고콕] 워크스페이스에 초대되었습니다");
            helper.setText(buildHtml(inviteUrl), true);
            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    private String buildHtml(String inviteUrl) {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
            <body style="margin:0;padding:0;background:#F2F4F6;font-family:-apple-system,BlinkMacSystemFont,'Apple SD Gothic Neo','Noto Sans KR',sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F2F4F6;padding:40px 0;">
                <tr><td align="center">
                  <table width="540" cellpadding="0" cellspacing="0" style="background:#FFFFFF;border-radius:16px;overflow:hidden;box-shadow:0 2px 16px rgba(25,31,40,.08);">
                    <!-- Header -->
                    <tr>
                      <td style="background:#3568F0;padding:28px 40px;text-align:center;">
                        <span style="font-size:22px;font-weight:900;color:#FFFFFF;letter-spacing:-.03em;">재고콕<span style="opacity:.7;">.</span></span>
                      </td>
                    </tr>
                    <!-- Body -->
                    <tr>
                      <td style="padding:40px 40px 32px;">
                        <h1 style="margin:0 0 12px;font-size:20px;font-weight:800;color:#191F28;letter-spacing:-.03em;">워크스페이스에 초대받으셨습니다 🎉</h1>
                        <p style="margin:0 0 24px;font-size:15px;color:#4E5968;line-height:1.6;">
                          재고콕 워크스페이스에 합류할 수 있는 초대장이 도착했습니다.<br>
                          아래 버튼을 클릭해 7일 이내에 가입을 완료하세요.
                        </p>
                        <!-- CTA Button -->
                        <table cellpadding="0" cellspacing="0" style="margin:0 auto 28px;">
                          <tr>
                            <td style="background:#3568F0;border-radius:12px;text-align:center;">
                              <a href="%s" style="display:inline-block;padding:14px 36px;font-size:15px;font-weight:700;color:#FFFFFF;text-decoration:none;letter-spacing:-.01em;">
                                팀 합류하기
                              </a>
                            </td>
                          </tr>
                        </table>
                        <p style="margin:0;font-size:13px;color:#8B95A1;text-align:center;">
                          버튼이 작동하지 않으면 아래 링크를 복사해 브라우저에 붙여넣으세요.<br>
                          <a href="%s" style="color:#3568F0;word-break:break-all;">%s</a>
                        </p>
                      </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                      <td style="background:#F2F4F6;padding:20px 40px;border-top:1px solid #E5E8EB;text-align:center;">
                        <p style="margin:0;font-size:12px;color:#8B95A1;">
                          이 메일은 재고콕 워크스페이스 초대로 자동 발송됐습니다.<br>
                          문의: <a href="mailto:hello@jaegokok.com" style="color:#3568F0;">hello@jaegokok.com</a>
                        </p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(inviteUrl, inviteUrl, inviteUrl);
    }
}
