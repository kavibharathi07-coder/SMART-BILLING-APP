USE supermarket_db;

CREATE INDEX idx_products_name
ON products(product_name);

CREATE INDEX idx_products_category_active
ON products(category_id, active);