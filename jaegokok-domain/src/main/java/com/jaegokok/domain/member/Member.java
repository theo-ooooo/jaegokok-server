package com.jaegokok.domain.member;

import com.jaegokok.core.member.MemberRole;
import com.jaegokok.core.member.MemberStatus;

import java.time.LocalDateTime;

public record Member(
        Long id,
        String email,
        String nickname,
        MemberRole role,
        MemberStatus status,
        LocalDateTime createdAt
) {}
