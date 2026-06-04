CREATE TABLE members (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    email      VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('ADMIN','USER') NOT NULL,
    status     ENUM('ACTIVE','WITHDRAWN') NOT NULL
);

CREATE TABLE refresh_tokens (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT NOT NULL UNIQUE,
    refresh_token VARCHAR(512) NOT NULL,
    expires_at    DATETIME(6) NOT NULL,
    created_at    DATETIME(6),
    updated_at    DATETIME(6)
);

CREATE TABLE workspaces (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    owner_id        BIGINT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(500),
    plan            ENUM('BASIC','FREE','PRO') NOT NULL,
    business_number VARCHAR(50),
    address         VARCHAR(500),
    phone           VARCHAR(50),
    CONSTRAINT fk_workspaces_owner FOREIGN KEY (owner_id) REFERENCES members (id)
);

CREATE TABLE workspace_members (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id BIGINT NOT NULL,
    member_id    BIGINT NOT NULL,
    role         ENUM('EMPLOYEE','OWNER') NOT NULL,
    UNIQUE KEY uk_workspace_members_workspace_member (workspace_id, member_id),
    CONSTRAINT fk_wm_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces (id),
    CONSTRAINT fk_wm_member    FOREIGN KEY (member_id)    REFERENCES members (id)
);

CREATE TABLE products (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    workspace_id    BIGINT NOT NULL,
    name            VARCHAR(200) NOT NULL,
    sku             VARCHAR(100),
    category        VARCHAR(100),
    unit            VARCHAR(50),
    description     VARCHAR(1000),
    price           DECIMAL(38,2),
    min_stock_level INT NOT NULL DEFAULT 0,
    current_stock   INT NOT NULL DEFAULT 0,
    qr_code         VARCHAR(36) NOT NULL UNIQUE,
    CONSTRAINT fk_products_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces (id)
);
