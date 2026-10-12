-- QUERY: PRODUCT_ADD
INSERT INTO products
(barcode, product_name, category_id, price,
 gst_rate, stock_qty, reorder_level, active)
VALUES (?, ?, ?, ?, ?, ?, ?, TRUE);

-- QUERY: PRODUCT_UPDATE
UPDATE products
SET product_name = ?,
    category_id = ?,
    price = ?,
    gst_rate = ?,
    reorder_level = ?
WHERE product_id = ?;

-- QUERY: PRODUCT_DEACTIVATE
UPDATE products
SET active = FALSE
WHERE product_id = ?;

-- QUERY: PRODUCT_RESTOCK
UPDATE products
SET stock_qty = stock_qty + ?
WHERE product_id = ?;

-- QUERY: CATEGORY_ADD
INSERT INTO categories (category_name)
VALUES (?);

-- QUERY: CATEGORY_GET_ALL
SELECT category_id, category_name
FROM categories
ORDER BY category_id;

-- QUERY: PRODUCT_FIND_BY_BARCODE
SELECT p.product_id, p.barcode, p.product_name, p.category_id, c.category_name,
       p.price, p.gst_rate, p.stock_qty, p.reorder_level, p.active
FROM products p JOIN categories c ON c.category_id = p.category_id
WHERE p.barcode = ?;

-- QUERY: PRODUCT_FIND_BY_ID
SELECT p.product_id, p.barcode, p.product_name, p.category_id, c.category_name,
       p.price, p.gst_rate, p.stock_qty, p.reorder_level, p.active
FROM products p JOIN categories c ON c.category_id = p.category_id
WHERE p.product_id = ?;

-- QUERY: PRODUCT_GET_ALL
SELECT p.product_id, p.barcode, p.product_name, p.category_id, c.category_name,
       p.price, p.gst_rate, p.stock_qty, p.reorder_level, p.active
FROM products p JOIN categories c ON c.category_id = p.category_id
ORDER BY p.product_name;

-- QUERY: PRODUCT_SEARCH
SELECT p.product_id, p.barcode, p.product_name, p.category_id, c.category_name,
       p.price, p.gst_rate, p.stock_qty, p.reorder_level, p.active
FROM products p JOIN categories c ON c.category_id = p.category_id
WHERE p.barcode LIKE CONCAT('%', ?, '%') OR p.product_name LIKE CONCAT('%', ?, '%')
ORDER BY p.product_name;
