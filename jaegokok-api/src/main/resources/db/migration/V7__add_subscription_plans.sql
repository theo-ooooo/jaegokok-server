CREATE TABLE subscription_plans (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_key      VARCHAR(20)  NOT NULL UNIQUE,
    name          VARCHAR(50)  NOT NULL,
    price_krw     INT          NOT NULL DEFAULT 0,
    product_limit INT          NOT NULL DEFAULT 50,
    member_limit  INT          NOT NULL DEFAULT 2,
    history_days  INT          NOT NULL DEFAULT 90,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

INSERT INTO subscription_plans (plan_key, name, price_krw, product_limit, member_limit, history_days) VALUES
    ('FREE',  '무료',   0,     50,  2, 90),
    ('BASIC', '베이직', 9900,  200, 5, 365),
    ('PRO',   '프로',   29000, -1,  -1, -1);
