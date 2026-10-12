USE supermarket_db;

DROP PROCEDURE IF EXISTS low_stock_cursor;

DELIMITER $$

CREATE PROCEDURE low_stock_cursor()
BEGIN

    DECLARE v_done BOOLEAN DEFAULT FALSE;

    DECLARE v_product_id INT;
    DECLARE v_barcode VARCHAR(50);
    DECLARE v_product_name VARCHAR(150);
    DECLARE v_stock_qty INT;
    DECLARE v_reorder_level INT;

    DECLARE low_stock_cursor CURSOR FOR

        SELECT
            product_id,
            barcode,
            product_name,
            stock_qty,
            reorder_level
        FROM products
        WHERE active = TRUE
          AND stock_qty <= reorder_level
        ORDER BY stock_qty ASC;

    DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_done = TRUE;


    DROP TEMPORARY TABLE IF EXISTS temp_low_stock;

    CREATE TEMPORARY TABLE temp_low_stock
    (
        product_id INT,
        barcode VARCHAR(50),
        product_name VARCHAR(150),
        stock_qty INT,
        reorder_level INT
    );


    OPEN low_stock_cursor;


    read_loop: LOOP

        FETCH low_stock_cursor
        INTO
            v_product_id,
            v_barcode,
            v_product_name,
            v_stock_qty,
            v_reorder_level;


        IF v_done THEN
            LEAVE read_loop;
        END IF;


        INSERT INTO temp_low_stock
        (
            product_id,
            barcode,
            product_name,
            stock_qty,
            reorder_level
        )
        VALUES
        (
            v_product_id,
            v_barcode,
            v_product_name,
            v_stock_qty,
            v_reorder_level
        );

    END LOOP;


    CLOSE low_stock_cursor;


    SELECT
        product_id,
        barcode,
        product_name,
        stock_qty,
        reorder_level
    FROM temp_low_stock
    ORDER BY stock_qty ASC;


    DROP TEMPORARY TABLE temp_low_stock;

END$$

DELIMITER ;