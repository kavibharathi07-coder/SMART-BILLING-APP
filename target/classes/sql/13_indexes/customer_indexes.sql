USE supermarket_db;

CREATE INDEX idx_customers_name
ON customers(customer_name);

CREATE INDEX idx_customers_phone
ON customers(phone);