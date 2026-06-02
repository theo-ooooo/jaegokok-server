package com.jaegokok.domain.workspace;

import java.time.LocalDateTime;

public record WorkspaceTrial(Long id, Long workspaceId, LocalDateTime startedAt, LocalDateTime endsAt) {

    public boolean isActive() {
        return LocalDateTime.now().isBefore(endsAt);
    }

    public int daysLeft() {
        if (!isActive()) return 0;
        long seconds = java.time.temporal.ChronoUnit.SECONDS.between(LocalDateTime.now(), endsAt);
        return (int) Math.max(0, (seconds + 86399) / 86400); // ceiling division
    }
}
