USE supermarket_db;

DROP PROCEDURE IF EXISTS bill_details;

DELIMITER $$

CREATE PROCEDURE bill_details(IN p_bill_id INT)
BEGIN

    SELECT
        b.bill_id,
        b.bill_no,
        b.created_at,
        c.customer_name,
        c.card_id,
        u.full_name AS biller_name,
        b.subtotal,
        b.discount,
        b.tax_total,
        b.grand_total,
        b.payment_mode,
        b.cash_received,
        b.change_amount,

        bi.bill_item_id,
        p.barcode,
        p.product_name,
        bi.quantity,
        bi.unit_price,
        bi.gst_rate,
        bi.tax_amount,
        bi.line_total

    FROM bills b

    LEFT JOIN customers c
        ON b.customer_id = c.customer_id

    INNER JOIN app_users u
        ON b.biller_id = u.user_id

    INNER JOIN bill_items bi
        ON b.bill_id = bi.bill_id

    INNER JOIN products p
        ON bi.product_id = p.product_id

    WHERE b.bill_id = p_bill_id

    ORDER BY bi.bill_item_id;

END$$

DELIMITER ;