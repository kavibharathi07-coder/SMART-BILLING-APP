-- QUERY: SEARCH_PRODUCTS
SELECT p.product_id, p.barcode, p.product_name,
       p.category_id, c.category_name, p.price, p.gst_rate,
       p.stock_qty, p.reorder_level, p.active
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
WHERE p.active = TRUE
  AND (
      p.barcode LIKE CONCAT('%', ?, '%')
      OR p.product_name LIKE CONCAT('%', ?, '%')
  )
ORDER BY p.product_name;