UPDATE workspaces SET name = company_name WHERE company_name IS NOT NULL AND company_name != '';
ALTER TABLE workspaces DROP COLUMN company_name;
