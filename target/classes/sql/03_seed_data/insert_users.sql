USE supermarket_db;

INSERT INTO app_users
(username, password_hash, full_name, role, active)
VALUES
(
    'admin',
    SHA2('admin123', 256),
    'Store Administrator',
    'ADMIN',
    TRUE
),
(
    'biller01',
    SHA2('biller123', 256),
    'Kavi',
    'BILLER',
    TRUE
),
(
    'biller02',
    SHA2('biller123', 256),
    'Arun',
    'BILLER',
    TRUE
),
(
    'inventory01',
    SHA2('inventory123', 256),
    'Inventory Staff',
    'INVENTORY',
    TRUE
);
