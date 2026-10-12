USE supermarket_db;

-- Run this once when upgrading an older installation created before the full SmartMart workflow.
ALTER TABLE app_users MODIFY role ENUM('ADMIN','BILLER','INVENTORY') NOT NULL;
ALTER TABLE bills MODIFY payment_mode ENUM('CASH','UPI','CARD') NOT NULL DEFAULT 'CASH';
ALTER TABLE bills ADD COLUMN IF NOT EXISTS transaction_ref VARCHAR(100);
ALTER TABLE products ADD COLUMN IF NOT EXISTS unit VARCHAR(30) NOT NULL DEFAULT 'Unit';
ALTER TABLE products ADD COLUMN IF NOT EXISTS expiry_tracking BOOLEAN NOT NULL DEFAULT FALSE;

SOURCE sql/02_schema/create_inventory_management.sql;
SOURCE sql/02_schema/create_discounts.sql;
SOURCE sql/02_schema/create_returns.sql;

INSERT IGNORE INTO app_users(username,password_hash,full_name,role,active)
VALUES ('inventory01', SHA2('inventory123',256), 'Inventory Staff', 'INVENTORY', TRUE);
