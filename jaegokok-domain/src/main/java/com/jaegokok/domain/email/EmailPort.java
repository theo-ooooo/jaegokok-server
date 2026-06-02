package com.jaegokok.domain.email;

public interface EmailPort {
    void sendInvitation(String toEmail, String inviteUrl);
}
