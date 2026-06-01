-- products 테이블에 current_stock 컬럼 추가
ALTER TABLE products
    ADD COLUMN current_stock INT NOT NULL DEFAULT 0;

-- inventory_records 테이블 생성
CREATE TABLE inventory_records
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    type       VARCHAR(10)  NOT NULL COMMENT 'IN | OUT',
    quantity   INT          NOT NULL,
    note       VARCHAR(500),
    created_by BIGINT       NOT NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_ir_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_ir_member  FOREIGN KEY (created_by) REFERENCES members (id)
);
