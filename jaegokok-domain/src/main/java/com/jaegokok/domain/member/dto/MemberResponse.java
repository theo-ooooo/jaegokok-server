package com.jaegokok.domain.member.dto;

import com.jaegokok.core.member.MemberRole;
import com.jaegokok.domain.member.Member;

public record MemberResponse(
        Long id,
        String email,
        String nickname,
        MemberRole role
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.id(), member.email(), member.nickname(), member.role());
    }
}
