USE supermarket_db;

START TRANSACTION;

INSERT INTO bills
(
    bill_no,
    customer_id,
    biller_id,
    subtotal,
    discount,
    tax_total,
    grand_total,
    payment_mode,
    cash_received,
    change_amount
)
VALUES
(
    CONCAT(
        'BILL-',
        DATE_FORMAT(NOW(), '%Y%m%d%H%i%s%f')
    ),
    1,
    2,
    560.00,
    0.00,
    25.00,
    585.00,
    'CASH',
    600.00,
    15.00
);

SET @new_bill_id = LAST_INSERT_ID();

INSERT INTO bill_items
(
    bill_id,
    product_id,
    quantity,
    unit_price,
    gst_rate,
    tax_amount,
    line_total
)
VALUES
(
    @new_bill_id,
    1,
    2,
    250.00,
    5.00,
    25.00,
    525.00
);

INSERT INTO bill_items
(
    bill_id,
    product_id,
    quantity,
    unit_price,
    gst_rate,
    tax_amount,
    line_total
)
VALUES
(
    @new_bill_id,
    2,
    1,
    60.00,
    0.00,
    0.00,
    60.00
);

UPDATE products
SET stock_qty = stock_qty - 2
WHERE product_id = 1;

UPDATE products
SET stock_qty = stock_qty - 1
WHERE product_id = 2;

COMMIT;