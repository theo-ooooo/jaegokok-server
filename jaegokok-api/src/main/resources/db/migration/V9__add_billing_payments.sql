CREATE TABLE billing_payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id    BIGINT       NOT NULL,
    order_id        VARCHAR(100) NOT NULL UNIQUE,
    payment_key     VARCHAR(200),
    plan_id         BIGINT       NOT NULL,
    amount          INT          NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    toss_response   TEXT,
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    billing_id      BIGINT,
    CONSTRAINT fk_billing_payments_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE,
    CONSTRAINT fk_billing_payments_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans (id),
    CONSTRAINT fk_billing_payments_billing FOREIGN KEY (billing_id) REFERENCES workspace_billings (id) ON DELETE SET NULL,
    INDEX idx_billing_payments_workspace_id (workspace_id),
    INDEX idx_billing_payments_order_id (order_id),
    INDEX idx_billing_payments_billing_id (billing_id)
);
