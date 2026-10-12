-- QUERY: GET_CUSTOMER_BY_ID
SELECT customer_id, card_id, customer_name, phone, email
FROM customers
WHERE card_id = ?;

-- QUERY: CUSTOMER_PURCHASE_HISTORY
SELECT b.bill_id, b.bill_no, b.created_at,
       b.subtotal, b.discount, b.tax_total,
       b.grand_total, b.payment_mode
FROM bills b
JOIN customers c ON b.customer_id = c.customer_id
WHERE c.card_id = ?
ORDER BY b.created_at DESC;

-- QUERY: GET_ALL_CUSTOMERS
SELECT customer_id, card_id, customer_name, phone, email
FROM customers
ORDER BY customer_name;