package com.jaegokok.domain.email;

public interface EmailPort {
    void sendInvitation(String toEmail, String inviteUrl);
    void sendPasswordReset(String toEmail, String resetUrl);
}
