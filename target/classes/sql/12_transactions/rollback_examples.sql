USE supermarket_db;

START TRANSACTION;

UPDATE products
SET price = price + 10
WHERE product_id = 1;

SELECT *
FROM products
WHERE product_id = 1;

ROLLBACK;

SELECT *
FROM products
WHERE product_id = 1;


