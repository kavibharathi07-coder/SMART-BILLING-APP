-- QUERY: FIND_BY_BARCODE
SELECT p.product_id, p.barcode, p.product_name,
       p.category_id, c.category_name, p.price, p.gst_rate,
       p.stock_qty, p.reorder_level, p.active
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
WHERE p.barcode = ?;

-- QUERY: FIND_BY_ID
SELECT p.product_id, p.barcode, p.product_name,
       p.category_id, c.category_name, p.price, p.gst_rate,
       p.stock_qty, p.reorder_level, p.active
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
WHERE p.product_id = ?;

-- QUERY: GET_ALL_PRODUCTS
SELECT p.product_id, p.barcode, p.product_name,
       p.category_id, c.category_name, p.price, p.gst_rate,
       p.stock_qty, p.reorder_level, p.active
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
ORDER BY p.product_name;

