/* =========================================================
   SUPERMARKET BILLING SYSTEM
   COMPLETE DATABASE SETUP
   MySQL 8.x
   ========================================================= */

CREATE DATABASE IF NOT EXISTS supermarket_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE supermarket_db;


/* =========================================================
   1. APP USERS
   ========================================================= */

CREATE TABLE IF NOT EXISTS app_users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,

    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,

    role ENUM('ADMIN', 'BILLER', 'INVENTORY') NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


/* =========================================================
   2. CATEGORIES
   ========================================================= */

CREATE TABLE IF NOT EXISTS categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,

    category_name VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB;


/* =========================================================
   3. CUSTOMERS
   ========================================================= */

CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,

    card_id VARCHAR(30) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL,

    phone VARCHAR(15),
    email VARCHAR(100),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


/* =========================================================
   4. PRODUCTS
   ========================================================= */

CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,

    barcode VARCHAR(50) UNIQUE NOT NULL,
    product_name VARCHAR(150) NOT NULL,

    category_id INT NOT NULL,

    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    gst_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,

    stock_qty INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 10,
    unit VARCHAR(30) NOT NULL DEFAULT 'Unit',
    expiry_tracking BOOLEAN NOT NULL DEFAULT FALSE,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id),

    CONSTRAINT chk_product_price
        CHECK (price >= 0),

    CONSTRAINT chk_product_gst
        CHECK (gst_rate >= 0),

    CONSTRAINT chk_product_stock
        CHECK (stock_qty >= 0),

    CONSTRAINT chk_product_reorder
        CHECK (reorder_level >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100), phone VARCHAR(20), email VARCHAR(120),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product_batches (
    batch_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL, supplier_id INT NULL, batch_no VARCHAR(60) NOT NULL,
    expiry_date DATE NULL, quantity_received INT NOT NULL, quantity_remaining INT NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0.00, received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    CHECK (quantity_received > 0), CHECK (quantity_remaining >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS stock_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL, user_id INT NULL, transaction_type ENUM('OPENING','PURCHASE','SALE','RETURN','ADJUSTMENT') NOT NULL,
    quantity INT NOT NULL, reference_id INT NULL, notes VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id), FOREIGN KEY (user_id) REFERENCES app_users(user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS discounts (
    discount_id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(40) UNIQUE NOT NULL, description VARCHAR(255), discount_type ENUM('PERCENT','FIXED') NOT NULL,
    value DECIMAL(10,2) NOT NULL, max_amount DECIMAL(10,2) NULL, start_date DATE NOT NULL, end_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE, CHECK (value >= 0), CHECK (end_date >= start_date)
) ENGINE=InnoDB;


/* =========================================================
   5. BILLS
   ========================================================= */

CREATE TABLE IF NOT EXISTS bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,

    bill_no VARCHAR(40) UNIQUE NOT NULL,

    customer_id INT NULL,
    biller_id INT NOT NULL,

    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    grand_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    payment_mode ENUM('CASH', 'UPI', 'CARD') NOT NULL DEFAULT 'CASH',

    cash_received DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    change_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    transaction_ref VARCHAR(100),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bill_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id),

    CONSTRAINT fk_bill_biller
        FOREIGN KEY (biller_id)
        REFERENCES app_users(user_id),

    CONSTRAINT chk_bill_subtotal
        CHECK (subtotal >= 0),

    CONSTRAINT chk_bill_discount
        CHECK (discount >= 0),

    CONSTRAINT chk_bill_tax
        CHECK (tax_total >= 0),

    CONSTRAINT chk_bill_grand_total
        CHECK (grand_total >= 0),

    CONSTRAINT chk_bill_cash_received
        CHECK (cash_received >= 0),

    CONSTRAINT chk_bill_change
        CHECK (change_amount >= 0)
) ENGINE=InnoDB;

/* =========================================================
   6. BILL ITEMS
   ========================================================= */

CREATE TABLE IF NOT EXISTS bill_items (
    bill_item_id INT PRIMARY KEY AUTO_INCREMENT,

    bill_id INT NOT NULL,
    product_id INT NOT NULL,

    quantity INT NOT NULL,

    unit_price DECIMAL(10,2) NOT NULL,
    gst_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,

    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    line_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_bill_item_bill
        FOREIGN KEY (bill_id)
        REFERENCES bills(bill_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bill_item_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id),

    CONSTRAINT chk_bill_item_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_bill_item_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_bill_item_gst
        CHECK (gst_rate >= 0),

    CONSTRAINT chk_bill_item_tax
        CHECK (tax_amount >= 0),

    CONSTRAINT chk_bill_item_total
        CHECK (line_total >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS returns (
    return_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL, bill_item_id INT NOT NULL, product_id INT NOT NULL, quantity INT NOT NULL,
    refund_amount DECIMAL(10,2) NOT NULL, reason VARCHAR(255), created_by INT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id), FOREIGN KEY (bill_item_id) REFERENCES bill_items(bill_item_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id), FOREIGN KEY (created_by) REFERENCES app_users(user_id),
    CHECK (quantity > 0), CHECK (refund_amount >= 0)
) ENGINE=InnoDB;


/* =========================================================
   7. AUDIT LOG
   ========================================================= */

CREATE TABLE IF NOT EXISTS audit_log (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,

    user_id INT NULL,

    entity_type VARCHAR(50),
    entity_id INT,

    action_type VARCHAR(50) NOT NULL,
    description VARCHAR(255),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_user
        FOREIGN KEY (user_id)
        REFERENCES app_users(user_id)
) ENGINE=InnoDB;


/* =========================================================
   INITIAL USERS
   ========================================================= */

INSERT IGNORE INTO app_users
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


/* =========================================================
   INITIAL CATEGORIES
   ========================================================= */

INSERT IGNORE INTO categories (category_name)
VALUES
('Grocery'),
('Beverages'),
('Snacks'),
('Personal Care'),
('Household'),
('Dairy');


/* =========================================================
   INITIAL PRODUCTS
   ========================================================= */

INSERT IGNORE INTO products
(
    barcode,
    product_name,
    category_id,
    price,
    gst_rate,
    stock_qty,
    reorder_level,
    active
)
VALUES
('890001', 'Aashirvaad Atta 5kg', 1, 250.00, 5.00, 40, 10, TRUE),
('890002', 'Milk 1L', 6, 60.00, 0.00, 100, 20, TRUE),
('890003', 'Bread', 6, 45.00, 0.00, 25, 10, TRUE),
('890004', 'Biscuit Pack', 3, 30.00, 18.00, 50, 10, TRUE),
('890005', 'Tea Powder 250g', 1, 120.00, 5.00, 30, 10, TRUE),
('890006', 'Soft Drink 750ml', 2, 45.00, 18.00, 35, 10, TRUE),
('890007', 'Bath Soap', 4, 40.00, 18.00, 60, 15, TRUE),
('890008', 'Detergent Powder 1kg', 5, 110.00, 18.00, 20, 8, TRUE);


/* =========================================================
   INITIAL CUSTOMERS
   ========================================================= */

INSERT IGNORE INTO customers
(
    card_id,
    customer_name,
    phone,
    email
)
VALUES
('CARD1001', 'Kavi', '9876543210', 'kavi@example.com'),
('CARD1002', 'Arun', '9876543211', 'arun@example.com'),
('CARD1003', 'Priya', '9876543212', 'priya@example.com'),
('CARD1004', 'Kamlesh', '9876543213', 'kamlesh@example.com');


/* =========================================================
   SETUP COMPLETE
   ========================================================= */
