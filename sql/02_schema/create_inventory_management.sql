USE supermarket_db;
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT, supplier_name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100), phone VARCHAR(20), email VARCHAR(120), active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS product_batches (
    batch_id INT PRIMARY KEY AUTO_INCREMENT, product_id INT NOT NULL, supplier_id INT NULL, batch_no VARCHAR(60) NOT NULL,
    expiry_date DATE NULL, quantity_received INT NOT NULL, quantity_remaining INT NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0.00, received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id), FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    CHECK (quantity_received > 0), CHECK (quantity_remaining >= 0)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS stock_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT, product_id INT NOT NULL, user_id INT NULL,
    transaction_type ENUM('OPENING','PURCHASE','SALE','RETURN','ADJUSTMENT') NOT NULL, quantity INT NOT NULL,
    reference_id INT NULL, notes VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id), FOREIGN KEY (user_id) REFERENCES app_users(user_id)
) ENGINE=InnoDB;
