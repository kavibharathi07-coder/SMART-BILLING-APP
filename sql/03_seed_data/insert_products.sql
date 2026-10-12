USE supermarket_db;

INSERT INTO products
(
    barcode,
    product_name,
    category_id,
    price,
    gst_rate,
    stock_qty,
    reorder_level,
    active
)
VALUES
('890001', 'Aashirvaad Atta 5kg', 1, 250.00, 5.00, 40, 10, TRUE),

('890002', 'Milk 1L', 6, 60.00, 0.00, 100, 20, TRUE),

('890003', 'Bread', 6, 45.00, 0.00, 25, 10, TRUE),

('890004', 'Biscuit Pack', 3, 30.00, 18.00, 50, 10, TRUE),

('890005', 'Tea Powder 250g', 1, 120.00, 5.00, 30, 10, TRUE),

('890006', 'Soft Drink 750ml', 2, 45.00, 18.00, 35, 10, TRUE),

('890007', 'Bath Soap', 4, 40.00, 18.00, 60, 15, TRUE),

('890008', 'Detergent Powder 1kg', 5, 110.00, 18.00, 20, 8, TRUE);