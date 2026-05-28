package com.jaegokok.domain.auth;

import com.jaegokok.core.auth.RefreshTokenEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtProvider {

    private final SecretKey signingKey;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;

    public JwtProvider(String secretKey, long accessTokenExpiryMs, long refreshTokenExpiryMs) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMs = accessTokenExpiryMs;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    public String generateAccessToken(Long memberId, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("role", role)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiryMs))
                .signWith(signingKey)
                .compact();
    }

    public RefreshTokenEntity generateRefreshToken(Long memberId) {
        Date now = new Date();
        Date ttlDate = new Date(now.getTime() + refreshTokenExpiryMs);

        String refreshToken = Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(ttlDate)
                .signWith(signingKey)
                .compact();

        return RefreshTokenEntity.of(memberId, refreshToken, refreshTokenExpiryMs / 1000);
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractMemberId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }
}
