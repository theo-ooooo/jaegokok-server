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

-- Backfill existing product images (replace 'jaegokok-dev' with actual S3 bucket name if different)
INSERT INTO images (entity_type, entity_id, original_path, bucket, created_at, updated_at)
SELECT 'PRODUCT', id, image_url, 'jaegokok-dev', NOW(6), NOW(6)
FROM products
WHERE image_url IS NOT NULL;

-- Backfill existing workspace logos (replace 'jaegokok-dev' with actual S3 bucket name if different)
INSERT INTO images (entity_type, entity_id, original_path, bucket, created_at, updated_at)
SELECT 'WORKSPACE', id, logo_url, 'jaegokok-dev', NOW(6), NOW(6)
FROM workspaces
WHERE logo_url IS NOT NULL;

ALTER TABLE products DROP COLUMN image_url;
ALTER TABLE workspaces DROP COLUMN logo_url;
