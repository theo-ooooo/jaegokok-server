package com.jaegokok.domain.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secretKey,
        long accessTokenExpiryMs,
        long refreshTokenExpiryMs
) {}
