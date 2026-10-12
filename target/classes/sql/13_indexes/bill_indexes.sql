USE supermarket_db;

CREATE INDEX idx_bills_customer_date
ON bills(customer_id, created_at);

CREATE INDEX idx_bills_biller_date
ON bills(biller_id, created_at);

CREATE INDEX idx_bill_items_bill_product
ON bill_items(bill_id, product_id);