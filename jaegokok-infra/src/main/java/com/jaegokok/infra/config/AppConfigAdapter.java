package com.jaegokok.infra.config;

import com.jaegokok.domain.app.AppConfigPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppConfigAdapter implements AppConfigPort {

    private final AppProperties appProperties;

    @Override
    public String getBaseUrl() {
        return appProperties.baseUrl();
    }
}
