CREATE TABLE workspace_invitations
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id BIGINT       NOT NULL,
    email        VARCHAR(200) NOT NULL,
    token        VARCHAR(100) NOT NULL UNIQUE,
    expires_at   DATETIME(6)  NOT NULL,
    used         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_workspace_invitations_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE,
    CONSTRAINT uq_workspace_invitation_email UNIQUE (workspace_id, email)
);
