package com.jaegokok.api.member;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.member.MemberService;
import com.jaegokok.domain.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public GlobalResponse<MemberResponse> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return GlobalResponse.success(HttpStatus.OK.value(), memberService.getMe(principal.getId()));
    }

    @DeleteMapping("/me")
    public GlobalResponse<Void> withdraw(@AuthenticationPrincipal UserPrincipal principal) {
        memberService.withdraw(principal.getId());
        return GlobalResponse.success(HttpStatus.OK.value(), null);
    }
}
