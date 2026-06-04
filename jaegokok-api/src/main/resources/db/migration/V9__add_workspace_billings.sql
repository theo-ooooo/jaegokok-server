CREATE TABLE workspace_billings (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id      BIGINT       NOT NULL UNIQUE,
    billing_key       VARCHAR(300) NOT NULL,
    customer_key      VARCHAR(100) NOT NULL,
    plan_key          VARCHAR(20)  NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    next_billing_date DATE         NOT NULL,
    created_at        DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at        DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_workspace_billings_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE
);
