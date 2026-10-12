USE supermarket_db;

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
