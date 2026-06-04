ALTER TABLE workspaces ADD COLUMN slug VARCHAR(50) UNIQUE;
-- Generate initial slugs from existing workspaces
UPDATE workspaces SET slug = CONCAT('workspace-', id) WHERE slug IS NULL;
ALTER TABLE workspaces MODIFY COLUMN slug VARCHAR(50) NOT NULL UNIQUE;
