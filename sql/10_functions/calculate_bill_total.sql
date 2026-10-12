USE supermarket_db;

DROP FUNCTION IF EXISTS calculate_bill_total;

DELIMITER $$

CREATE FUNCTION calculate_bill_total(
    p_bill_id INT
)
RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN

    DECLARE v_total DECIMAL(10,2);

    SELECT COALESCE(SUM(line_total), 0.00)
    INTO v_total
    FROM bill_items
    WHERE bill_id = p_bill_id;

    RETURN v_total;

END$$

DELIMITER ;