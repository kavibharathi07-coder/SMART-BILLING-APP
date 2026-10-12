USE supermarket_db;

DROP FUNCTION IF EXISTS calculate_tax;

DELIMITER $$

CREATE FUNCTION calculate_tax(
    p_amount DECIMAL(10,2),
    p_gst_rate DECIMAL(5,2)
)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN

    RETURN ROUND(
        p_amount * p_gst_rate / 100,
        2
    );

END$$

DELIMITER ;