USE supermarket_db;

DROP TRIGGER IF EXISTS after_bill_insert_audit;

DELIMITER $$

CREATE TRIGGER after_bill_insert_audit
AFTER INSERT ON bills
FOR EACH ROW
BEGIN

    INSERT INTO audit_log
    (
        user_id,
        entity_type,
        entity_id,
        action_type,
        description
    )
    VALUES
    (
        NEW.biller_id,
        'BILL',
        NEW.bill_id,
        'CREATE',
        CONCAT(
            'Bill created: ',
            NEW.bill_no,
            ', Grand Total: ',
            NEW.grand_total
        )
    );

END$$

DELIMITER ;