USE supermarket_db;

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