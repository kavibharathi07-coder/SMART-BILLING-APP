USE supermarket_db;
CREATE TABLE IF NOT EXISTS discounts (
    discount_id INT PRIMARY KEY AUTO_INCREMENT, code VARCHAR(40) UNIQUE NOT NULL, description VARCHAR(255),
    discount_type ENUM('PERCENT','FIXED') NOT NULL, value DECIMAL(10,2) NOT NULL, max_amount DECIMAL(10,2) NULL,
    start_date DATE NOT NULL, end_date DATE NOT NULL, active BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (value >= 0), CHECK (end_date >= start_date)
) ENGINE=InnoDB;
