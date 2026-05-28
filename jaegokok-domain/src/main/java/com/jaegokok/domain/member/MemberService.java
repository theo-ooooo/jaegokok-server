package com.jaegokok.domain.member;

import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.ErrorCode;
import com.jaegokok.domain.auth.JwtProvider;
import com.jaegokok.domain.member.dto.LoginRequest;
import com.jaegokok.domain.member.dto.LoginResponse;
import com.jaegokok.domain.member.dto.MemberResponse;
import com.jaegokok.domain.member.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = memberRepository.register(request.email(), encodedPassword, request.nickname());
        return MemberResponse.from(member);
    }

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_UNAUTHORIZED));

        String encodedPassword = memberRepository.findEncodedPasswordByEmail(request.email());
        if (!passwordEncoder.matches(request.password(), encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        String accessToken = jwtProvider.generateAccessToken(member.id(), member.role().name());
        String refreshToken = jwtProvider.generateRefreshToken(member.id());

        return new LoginResponse(accessToken, refreshToken, member.nickname());
    }

    public MemberResponse getMe(Long memberId) {
        return MemberResponse.from(memberRepository.findById(memberId));
    }

    @Transactional
    public void withdraw(Long memberId) {
        memberRepository.withdraw(memberId);
    }
}
