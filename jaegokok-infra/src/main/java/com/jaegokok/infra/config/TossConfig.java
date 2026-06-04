package com.jaegokok.infra.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({TossProperties.class, AppProperties.class})
public class TossConfig {}
