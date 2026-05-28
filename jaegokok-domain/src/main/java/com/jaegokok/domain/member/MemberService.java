package com.jaegokok.domain.member;

import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.ErrorCode;
import com.jaegokok.core.auth.RefreshTokenEntity;
import com.jaegokok.domain.auth.JwtProvider;
import com.jaegokok.domain.auth.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
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

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_UNAUTHORIZED));

        String encodedPassword = memberRepository.findEncodedPasswordByEmail(request.email());
        if (!passwordEncoder.matches(request.password(), encodedPassword)) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        String accessToken = jwtProvider.generateAccessToken(member.id(), member.role().name());
        RefreshTokenEntity refreshTokenEntity = jwtProvider.generateRefreshToken(member.id());

        refreshTokenRepository.save(refreshTokenEntity);

        return LoginResponse.from(accessToken, refreshTokenEntity.getRefreshToken(), member.nickname());
    }

    public MemberResponse getMe(Long memberId) {
        return MemberResponse.from(memberRepository.findById(memberId));
    }

    @Transactional
    public void withdraw(Long memberId) {
        memberRepository.withdraw(memberId);
    }

    @Transactional
    public LoginResponse reissue(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        try {
            jwtProvider.parseToken(refreshToken);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_EXPIRED));

        Long memberId = refreshTokenEntity.getMemberId();
        Member member = memberRepository.findById(memberId);

        String newAccessToken = jwtProvider.generateAccessToken(member.id(), member.role().name());
        RefreshTokenEntity newRefreshTokenEntity = jwtProvider.generateRefreshToken(member.id());

        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        refreshTokenRepository.save(newRefreshTokenEntity);

        return LoginResponse.from(newAccessToken, newRefreshTokenEntity.getRefreshToken(), member.nickname());
    }
}
