USE supermarket_db;

DROP PROCEDURE IF EXISTS daily_sales;

DELIMITER $$

CREATE PROCEDURE daily_sales(IN p_sales_date DATE)
BEGIN

    SELECT
        p_sales_date AS sales_date,
        COUNT(*) AS total_bills,
        COALESCE(SUM(subtotal), 0.00) AS total_subtotal,
        COALESCE(SUM(discount), 0.00) AS total_discount,
        COALESCE(SUM(tax_total), 0.00) AS total_tax,
        COALESCE(SUM(grand_total), 0.00) AS total_sales
    FROM bills
    WHERE DATE(created_at) = p_sales_date;

END$$

DELIMITER ;