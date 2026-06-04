CREATE TABLE password_reset_tokens (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT NOT NULL,
    token      VARCHAR(100) NOT NULL UNIQUE,
    expires_at DATETIME(6) NOT NULL,
    used       BIT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_prt_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE,
    INDEX idx_prt_token (token)
);
