package com.jaegokok.api.auth;

import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.member.MemberService;
import com.jaegokok.domain.member.dto.LoginRequest;
import com.jaegokok.domain.member.dto.LoginResponse;
import com.jaegokok.domain.member.dto.MemberResponse;
import com.jaegokok.domain.member.dto.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse<MemberResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), memberService.signUp(request));
    }

    @PostMapping("/login")
    public GlobalResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return GlobalResponse.success(HttpStatus.OK.value(), memberService.login(request));
    }

    @PostMapping("/refresh")
    public GlobalResponse<LoginResponse> reissue(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        return GlobalResponse.success(HttpStatus.OK.value(), memberService.reissue(refreshToken));
    }
}
