USE supermarket_db;

DROP PROCEDURE IF EXISTS low_stock_products;

DELIMITER $$

CREATE PROCEDURE low_stock_products()
BEGIN

    SELECT
        p.product_id,
        p.barcode,
        p.product_name,
        c.category_name,
        p.stock_qty,
        p.reorder_level,
        p.price
    FROM products p

    INNER JOIN categories c
        ON p.category_id = c.category_id

    WHERE p.active = TRUE
      AND p.stock_qty <= p.reorder_level

    ORDER BY p.stock_qty ASC;

END$$

DELIMITER ;