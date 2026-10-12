USE supermarket_db;

DROP PROCEDURE IF EXISTS biller_history;

DELIMITER $$

CREATE PROCEDURE biller_history(IN p_biller_id INT)
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
    WHERE b.biller_id = p_biller_id
    ORDER BY b.created_at DESC;

END$$

DELIMITER ;