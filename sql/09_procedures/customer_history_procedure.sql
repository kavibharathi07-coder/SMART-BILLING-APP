USE supermarket_db;

DROP PROCEDURE IF EXISTS customer_history;

DELIMITER $$

CREATE PROCEDURE customer_history(IN p_customer_id INT)
BEGIN

    SELECT
        b.bill_id,
        b.bill_no,
        b.created_at,
        b.subtotal,
        b.discount,
        b.tax_total,
        b.grand_total,
        b.payment_mode
    FROM bills b
    WHERE b.customer_id = p_customer_id
    ORDER BY b.created_at DESC;

END$$

DELIMITER ;