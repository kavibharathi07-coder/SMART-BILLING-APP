USE supermarket_db;
CREATE TABLE IF NOT EXISTS returns (
    return_id INT PRIMARY KEY AUTO_INCREMENT, bill_id INT NOT NULL, bill_item_id INT NOT NULL, product_id INT NOT NULL,
    quantity INT NOT NULL, refund_amount DECIMAL(10,2) NOT NULL, reason VARCHAR(255), created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bill_id) REFERENCES bills(bill_id),
    FOREIGN KEY (bill_item_id) REFERENCES bill_items(bill_item_id), FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (created_by) REFERENCES app_users(user_id), CHECK (quantity > 0), CHECK (refund_amount >= 0)
) ENGINE=InnoDB;
