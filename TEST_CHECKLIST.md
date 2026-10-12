# SmartMart Console Application Test Checklist

## 1. Environment

- Start MySQL Server 8.x.
- Confirm `src/main/resources/db.properties` uses the actual MySQL root password.
- Run the master setup for a new database, or the migration for an existing database.
- Install the audit and stock-validation triggers.
- Run the application with Java 21 or compile with `javac --release 21`.

Default accounts:

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Biller | `biller01` | `biller123` |
| Inventory | `inventory01` | `inventory123` |

## 2. Startup and authentication

- Start the application and confirm the main menu appears.
- Choose `3. Exit`; confirm clean shutdown.
- Login with each default account; confirm the correct dashboard opens.
- Enter an incorrect password; confirm login fails without terminating the application.
- Confirm inactive users cannot log in.

## 3. Biller workflow

- Use `Search Products` with a barcode such as `890001`.
- Use `Search Products` with part of a name such as `milk` or `kavi`.
- Confirm the result shows price, GST, stock, and active status.
- Create a bill for a walk-in customer.
- Create a bill for an existing card such as `CARD1001`.
- Add at least two different products and quantities greater than one.
- View the cart, remove an item, and confirm totals update.
- Try a quantity greater than available stock; confirm the item is rejected.
- Apply a valid discount; confirm subtotal, tax, discount, and total are shown.
- Try a discount greater than the subtotal; confirm checkout is rejected.
- Complete a CASH payment with enough cash; confirm change is calculated.
- Complete a UPI payment with a transaction reference.
- Complete a CARD payment with a transaction reference.
- Confirm a bill number and invoice path are printed.
- Confirm an invoice text file is created in `invoices`.
- Confirm the product stock decreases after a successful bill.
- Confirm the stock does not change when checkout fails.
- Open `View Bill Details` using a bill number and a numeric bill ID.
- Open `View Previous Bills` and confirm the new sale appears.
- Open `View Discounts`; confirm configured discount records display.

## 4. Customer workflow

- Open Customer Management and list customers.
- Add a new customer with card ID, name, phone, and email.
- Edit the new customer and confirm the changes persist.
- Create a bill for the new customer.
- View that customer’s purchase history.
- Try a duplicate card ID; confirm the database rejects it cleanly.

## 5. Returns and refunds

- Create a bill with at least one product.
- Note the bill number and bill item ID from bill details.
- Open Returns / Refunds.
- Enter the bill number; confirm sold items and quantities are displayed.
- Return a valid quantity and reason.
- Confirm the return succeeds and stock is restored.
- Try returning more than the sold quantity; confirm it is rejected.
- Try an invalid bill number; confirm the dashboard remains open.

## 6. Inventory workflow

- Login as `inventory01`.
- View current stock.
- Receive stock with product ID, supplier ID, batch number, expiry date, quantity, and purchase price.
- Confirm product stock increases.
- Confirm the batch appears in Batches / Expiry.
- Confirm the purchase appears in Stock History.
- Search inventory by product name and barcode.
- Open Low-stock Alerts and confirm low-stock products appear.
- Add a batch expiring within 30 days and confirm it appears in the expiry list.

## 7. Admin workflow

- Open Product Management and list products.
- Add a product using an existing category.
- Update its name, price, GST, and reorder level.
- Deactivate it and confirm it cannot be billed.
- Restock it and confirm stock changes.
- Add a category and confirm it appears in category lists.
- Add a supplier and confirm it appears in Supplier Management.
- Add a BILLER employee and an INVENTORY employee.
- Deactivate an employee and confirm that employee cannot log in.
- Add a percentage discount and a fixed discount.
- Confirm discount dates and active status display.
- Deactivate a discount and confirm its status changes.
- Open Sales Reports and test Daily Sales, Biller Sales, Low Stock, and Audit Logs.
- Open System Summary and confirm today’s bill count, sales, and active product count.

## 8. Database verification queries

```sql
USE supermarket_db;
SHOW TABLES;
SELECT username, role, active FROM app_users;
SELECT product_id, product_name, stock_qty FROM products ORDER BY product_id;
SELECT bill_no, payment_mode, transaction_ref, grand_total FROM bills ORDER BY bill_id DESC;
SELECT * FROM bill_items ORDER BY bill_item_id DESC;
SELECT * FROM stock_transactions ORDER BY transaction_id DESC;
SELECT * FROM product_batches ORDER BY batch_id DESC;
SELECT * FROM returns ORDER BY return_id DESC;
SELECT * FROM audit_log ORDER BY audit_id DESC;
```

## 9. Expected final result

- Java compilation completes with no errors.
- The app launches using Java 21.
- Each role opens only its permitted dashboard.
- Sales, stock, payments, invoices, returns, discounts, customers, suppliers, employees, batches, and audit records persist in MySQL.
- Invalid input shows an operation error and returns to the current dashboard instead of terminating the application.
