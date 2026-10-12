-- QUERY: BILL_CREATE
INSERT INTO bills
    (bill_no, customer_id, biller_id, subtotal, discount, tax_total,
     grand_total, payment_mode, cash_received, change_amount, transaction_ref)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

-- QUERY: BILL_ITEM_CREATE
INSERT INTO bill_items
    (bill_id, product_id, quantity, unit_price, gst_rate, tax_amount, line_total)
VALUES (?, ?, ?, ?, ?, ?, ?)

-- QUERY: BILL_FIND_BY_ID
SELECT *
FROM bills
WHERE bill_id = ?

-- QUERY: BILL_FIND_BY_NUMBER
SELECT *
FROM bills
WHERE bill_no = ?

-- QUERY: BILL_ITEMS_BY_BILL
SELECT bi.*, p.product_name
FROM bill_items bi
JOIN products p ON p.product_id = bi.product_id
WHERE bi.bill_id = ?
ORDER BY bi.bill_item_id

-- QUERY: PRODUCT_DEDUCT_STOCK
UPDATE products
SET stock_qty = stock_qty - ?
WHERE product_id = ?
  AND active = TRUE
  AND stock_qty >= ?

-- QUERY: STOCK_RECORD_SALE
INSERT INTO stock_transactions
    (product_id, transaction_type, quantity, notes)
VALUES (?, 'SALE', ?, ?)

-- QUERY: CUSTOMER_BILL_HISTORY
SELECT b.bill_no,
       DATE_FORMAT(b.created_at, '%Y-%m-%d %H:%i:%s') AS created_at,
       u.full_name,
       b.grand_total
FROM bills b
JOIN customers c ON b.customer_id = c.customer_id
JOIN app_users u ON b.biller_id = u.user_id
WHERE c.card_id = ?
ORDER BY b.created_at DESC

-- QUERY: BILLER_BILL_HISTORY
SELECT b.bill_no,
       DATE_FORMAT(b.created_at, '%Y-%m-%d %H:%i:%s') AS created_at,
       COALESCE(c.customer_name, 'WALK-IN') AS customer_name,
       b.grand_total
FROM bills b
LEFT JOIN customers c ON b.customer_id = c.customer_id
WHERE b.biller_id = ?
ORDER BY b.created_at DESC

-- QUERY: CUSTOMER_CREATE
INSERT INTO customers (card_id, customer_name, phone, email)
VALUES (?, ?, ?, ?)

-- QUERY: CUSTOMER_UPDATE
UPDATE customers
SET customer_name = ?, phone = ?, email = ?
WHERE card_id = ?

-- QUERY: SUPPLIER_LIST
SELECT supplier_id, supplier_name, contact_person, phone, email, active
FROM suppliers
ORDER BY supplier_name

-- QUERY: SUPPLIER_CREATE
INSERT INTO suppliers (supplier_name, contact_person, phone, email)
VALUES (?, ?, ?, ?)

-- QUERY: SUPPLIER_DEACTIVATE
UPDATE suppliers
SET active = FALSE
WHERE supplier_id = ?

-- QUERY: BATCH_CREATE
INSERT INTO product_batches
    (product_id, supplier_id, batch_no, expiry_date, quantity_received,
     quantity_remaining, purchase_price)
VALUES (?, ?, ?, ?, ?, ?, ?)

-- QUERY: PRODUCT_ADD_STOCK
UPDATE products
SET stock_qty = stock_qty + ?
WHERE product_id = ?

-- QUERY: STOCK_RECORD_PURCHASE
INSERT INTO stock_transactions (product_id, transaction_type, quantity, notes)
VALUES (?, 'PURCHASE', ?, ?)

-- QUERY: BATCH_LIST
SELECT b.batch_id,
       p.product_name,
       b.batch_no,
       b.expiry_date,
       b.quantity_remaining,
       s.supplier_name
FROM product_batches b
JOIN products p ON p.product_id = b.product_id
LEFT JOIN suppliers s ON s.supplier_id = b.supplier_id
WHERE b.quantity_remaining > 0
ORDER BY b.expiry_date

-- QUERY: BATCH_LIST_EXPIRING
SELECT b.batch_id,
       p.product_name,
       b.batch_no,
       b.expiry_date,
       b.quantity_remaining,
       s.supplier_name
FROM product_batches b
JOIN products p ON p.product_id = b.product_id
LEFT JOIN suppliers s ON s.supplier_id = b.supplier_id
WHERE b.quantity_remaining > 0
  AND b.expiry_date IS NOT NULL
  AND b.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)
ORDER BY b.expiry_date

-- QUERY: STOCK_HISTORY
SELECT t.transaction_id,
       p.product_name,
       t.transaction_type,
       t.quantity,
       t.notes,
       t.created_at
FROM stock_transactions t
JOIN products p ON p.product_id = t.product_id
ORDER BY t.created_at DESC
LIMIT 100

-- QUERY: DISCOUNT_LIST
SELECT *
FROM discounts
ORDER BY active DESC, end_date DESC

-- QUERY: DISCOUNT_CREATE
INSERT INTO discounts
    (code, description, discount_type, value, max_amount, start_date, end_date)
VALUES (?, ?, ?, ?, ?, ?, ?)

-- QUERY: DISCOUNT_DEACTIVATE
UPDATE discounts
SET active = FALSE
WHERE discount_id = ?

-- QUERY: EMPLOYEE_LIST
SELECT user_id, username, full_name, role, active, created_at
FROM app_users
ORDER BY full_name

-- QUERY: EMPLOYEE_CREATE
INSERT INTO app_users (username, password_hash, full_name, role)
VALUES (?, ?, ?, ?)

-- QUERY: EMPLOYEE_DEACTIVATE
UPDATE app_users
SET active = FALSE
WHERE user_id = ?

-- QUERY: RETURN_FIND_BILL_ITEM
SELECT bi.bill_id, bi.product_id, bi.quantity, bi.line_total
FROM bill_items bi
WHERE bi.bill_item_id = ?

-- QUERY: RETURN_CREATE
INSERT INTO returns
    (bill_id, bill_item_id, product_id, quantity, refund_amount, reason, created_by)
VALUES (?, ?, ?, ?, ?, ?, ?)

-- QUERY: RETURN_ADD_STOCK
UPDATE products
SET stock_qty = stock_qty + ?
WHERE product_id = ?

-- QUERY: STOCK_RECORD_RETURN
INSERT INTO stock_transactions
    (product_id, user_id, transaction_type, quantity, reference_id, notes)
VALUES (?, ?, 'RETURN', ?, ?, ?)

-- QUERY: REPORT_DAILY_SALES
SELECT COUNT(*) AS total_bills,
       COALESCE(SUM(subtotal), 0) AS subtotal,
       COALESCE(SUM(discount), 0) AS discount,
       COALESCE(SUM(tax_total), 0) AS tax,
       COALESCE(SUM(grand_total), 0) AS total_sales,
       COALESCE(SUM(cash_received), 0) AS cash_collected
FROM bills
WHERE DATE(created_at) = ?

-- QUERY: REPORT_BILLER_SALES
SELECT u.full_name AS biller,
       COUNT(b.bill_id) AS bills,
       COALESCE(SUM(b.grand_total), 0) AS sales
FROM app_users u
LEFT JOIN bills b ON u.user_id = b.biller_id
WHERE u.role IN ('ADMIN', 'BILLER')
GROUP BY u.user_id, u.full_name
ORDER BY sales DESC

-- QUERY: REPORT_LOW_STOCK
SELECT product_id, product_name, stock_qty, reorder_level
FROM products
WHERE active = TRUE
  AND stock_qty <= reorder_level
ORDER BY stock_qty ASC
LIMIT ?

-- QUERY: REPORT_AUDIT_LOGS
SELECT audit_id, user_id, action_type, description, created_at
FROM audit_log
ORDER BY created_at DESC
LIMIT 50
