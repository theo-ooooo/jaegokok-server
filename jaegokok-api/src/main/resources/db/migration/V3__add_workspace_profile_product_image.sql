ALTER TABLE workspaces
    ADD COLUMN company_name    VARCHAR(200),
    ADD COLUMN business_number VARCHAR(50),
    ADD COLUMN address         VARCHAR(500),
    ADD COLUMN phone           VARCHAR(50),
    ADD COLUMN logo_url        VARCHAR(500);

ALTER TABLE products
    ADD COLUMN image_url VARCHAR(500);
