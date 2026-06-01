CREATE TABLE workspace_images
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id  BIGINT       NOT NULL,
    original_path VARCHAR(500) NOT NULL,
    webp_path     VARCHAR(500),
    bucket        VARCHAR(100) NOT NULL,
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_wi_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE
);

ALTER TABLE workspaces
    DROP COLUMN logo_url;
