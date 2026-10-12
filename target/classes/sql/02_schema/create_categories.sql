USE supermarket_db;

CREATE TABLE IF NOT EXISTS categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,

    category_name VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB;