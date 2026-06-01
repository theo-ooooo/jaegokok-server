package com.jaegokok.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "qr")
public record QrProperties(String baseUrl) {}
