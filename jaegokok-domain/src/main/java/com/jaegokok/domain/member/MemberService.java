package com.jaegokok.domain.member;

import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.ErrorCode;
import com.jaegokok.domain.auth.JwtProvider;
import com.jaegokok.domain.auth.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import com.jaegokok.domain.member.dto.LoginRequest;
import com.jaegokok.domain.member.dto.LoginResult;
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
    private final RefreshTokenRepository refreshTokenRepository;
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

    public LoginResult login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_UNAUTHORIZED));

        String encodedPassword = memberRepository.findEncodedPasswordByEmail(request.email());
        if (!passwordEncoder.matches(request.password(), encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        String accessToken = jwtProvider.generateAccessToken(member.id(), member.role().name());
        String refreshToken = jwtProvider.generateRefreshToken(member.id());
        long ttlSeconds = jwtProvider.getRefreshTokenTtlSeconds();
        refreshTokenRepository.save(member.id(), refreshToken, ttlSeconds);

        return new LoginResult(accessToken, refreshToken, member.nickname(), ttlSeconds);
    }

    public MemberResponse getMe(Long memberId) {
        return MemberResponse.from(memberRepository.findById(memberId));
    }

    @Transactional
    public void withdraw(Long memberId) {
        memberRepository.withdraw(memberId);
    }

    public LoginResult reissue(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        try {
            jwtProvider.parseToken(refreshToken);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        Long memberId = refreshTokenRepository.findMemberIdByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_EXPIRED));

        Member member = memberRepository.findById(memberId);

        String newAccessToken = jwtProvider.generateAccessToken(member.id(), member.role().name());
        String newRefreshToken = jwtProvider.generateRefreshToken(member.id());
        long ttlSeconds = jwtProvider.getRefreshTokenTtlSeconds();

        refreshTokenRepository.deleteByToken(refreshToken);
        refreshTokenRepository.save(member.id(), newRefreshToken, ttlSeconds);

        return new LoginResult(newAccessToken, newRefreshToken, member.nickname(), ttlSeconds);
    }

    public void logout(String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
