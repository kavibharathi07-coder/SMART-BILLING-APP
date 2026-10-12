USE supermarket_db;

START TRANSACTION;

UPDATE products
SET stock_qty = stock_qty - 2
WHERE product_id = 1;

SAVEPOINT stock_update;

UPDATE products
SET price = price + 20
WHERE product_id = 1;

ROLLBACK TO stock_update;

COMMIT;