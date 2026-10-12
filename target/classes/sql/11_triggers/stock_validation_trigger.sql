USE supermarket_db;

DROP TRIGGER IF EXISTS before_bill_item_insert;

DELIMITER $$

CREATE TRIGGER before_bill_item_insert
BEFORE INSERT ON bill_items
FOR EACH ROW
BEGIN

    DECLARE v_stock INT DEFAULT 0;

    SELECT stock_qty
    INTO v_stock
    FROM products
    WHERE product_id = NEW.product_id;

    IF v_stock < NEW.quantity THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock for this product';

    END IF;

END$$

DELIMITER ;

USE supermarket_db;

DROP TRIGGER IF EXISTS before_bill_item_update;

DELIMITER $$

CREATE TRIGGER before_bill_item_update
BEFORE UPDATE ON bill_items
FOR EACH ROW
BEGIN

    DECLARE v_stock INT DEFAULT 0;

    IF NEW.product_id = OLD.product_id THEN

        SELECT stock_qty
        INTO v_stock
        FROM products
        WHERE product_id = NEW.product_id;

        IF NEW.quantity > OLD.quantity
           AND v_stock < (NEW.quantity - OLD.quantity) THEN

            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Insufficient stock for quantity increase';

        END IF;

    ELSE

        SELECT stock_qty
        INTO v_stock
        FROM products
        WHERE product_id = NEW.product_id;

        IF v_stock < NEW.quantity THEN

            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Insufficient stock for new product';

        END IF;

    END IF;

END$$

DELIMITER ;