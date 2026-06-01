CREATE TABLE images
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type   VARCHAR(50)  NOT NULL COMMENT 'PRODUCT | WORKSPACE',
    entity_id     BIGINT       NOT NULL,
    original_path VARCHAR(500) NOT NULL,
    webp_path     VARCHAR(500),
    bucket        VARCHAR(100) NOT NULL,
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_images_entity (entity_type, entity_id)
);

ALTER TABLE products DROP COLUMN image_url;
ALTER TABLE workspaces DROP COLUMN logo_url;
