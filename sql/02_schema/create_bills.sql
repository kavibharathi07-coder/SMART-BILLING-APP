USE supermarket_db;

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
