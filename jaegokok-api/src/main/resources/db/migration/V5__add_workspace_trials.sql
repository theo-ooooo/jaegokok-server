CREATE TABLE workspace_trials
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id BIGINT      NOT NULL UNIQUE,
    started_at   DATETIME(6) NOT NULL,
    ends_at      DATETIME(6) NOT NULL,
    created_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_workspace_trials_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE,
    INDEX idx_workspace_trials_workspace_id (workspace_id)
);
