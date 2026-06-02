package com.jaegokok.domain.workspace;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record WorkspaceTrial(Long id, Long workspaceId, LocalDateTime startedAt, LocalDateTime endsAt) {

    public boolean isActive() {
        return LocalDateTime.now().isBefore(endsAt);
    }

    public int daysLeft() {
        return (int) Math.max(0, ChronoUnit.DAYS.between(LocalDateTime.now(), endsAt));
    }
}
