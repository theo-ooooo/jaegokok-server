package com.jaegokok.domain.workspace.dto;

import com.jaegokok.domain.member.Member;

public record AddEmployeeResponse(
        Long memberId,
        String email,
        String nickname
) {
    public static AddEmployeeResponse from(Member member) {
        return new AddEmployeeResponse(member.id(), member.email(), member.nickname());
    }
}
