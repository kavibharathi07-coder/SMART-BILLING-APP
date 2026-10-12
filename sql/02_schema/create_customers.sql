USE supermarket_db;

CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,

    card_id VARCHAR(30) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL,

    phone VARCHAR(15),
    email VARCHAR(100),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;