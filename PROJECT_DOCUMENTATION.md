# Smart Billing App — Production Architecture and Operations Documentation

**Document status:** Implementation-grounded handoff | **Repository version:** `1.0-SNAPSHOT` | **Last reviewed:** 2026-10-11

This document describes the application currently in this repository and identifies the controls that must be added before treating it as a production multi-user service. The current product is a Java console application, not an HTTP/API deployment. Where the requested production-grade control is not present in the code, it is marked **Required hardening** rather than implied.

## 1. Executive Summary & Core Architecture

### Project purpose

Smart Billing App is a supermarket point-of-sale and inventory management system for small and medium retail stores. It lets staff authenticate, search products, create multi-item cash/card/UPI bills, associate a bill with a customer card, decrement stock atomically, produce a text invoice, manage products and inventory, process returns, and review sales, customer, biller, low-stock, and audit reports.

Primary users are:

- **Administrator:** catalog, categories, suppliers, batches, discounts, employees, customer records, reports, audit logs, and system summary.
- **Biller:** product lookup, customer lookup/registration, billing, invoice generation, and billing/customer history.
- **Inventory staff:** inventory-oriented product and stock operations exposed by the inventory menu.
- **General user:** read-only/customer-facing history access exposed from the landing menu.

### Technology stack

| Area | Implementation |
|---|---|
| Runtime | Java 21 configured by Maven (`maven.compiler.source/target=21`; README still says Java 17+) |
| Build | Maven, artifact `com.supermarket:smart-billing-app:1.0-SNAPSHOT` |
| User interface | ANSI-styled interactive console; `Scanner`-based input |
| Application architecture | Single-process layered application: UI → service → DAO → JDBC/MySQL |
| Persistence | MySQL 8.x / InnoDB, database `supermarket_db`, `utf8mb4` |
| Database driver | MySQL Connector/J 9.4.0 |
| SQL capabilities | Tables, foreign keys, checks, indexes, views, stored procedures, functions, triggers, transactions, reports, and cursor examples |
| Local artifacts | UTF-8 text invoices under `invoices/<bill_no>.txt` |
| External APIs | None in the current implementation; payment modes are recorded values, not gateway integrations |
| Hosting | No container, service wrapper, cloud infrastructure, CI/CD pipeline, pool, or HTTP server is defined in the repository |

### System architecture

The runtime is a modular monolith with a direct database connection per DAO operation:

text
Console user
   │
   ▼
UI menus (Login, Admin, Biller, Inventory, Billing, Reports)
   │ validates input and selects use case
   ▼
Services (Auth, Billing, Product, Customer, Management, History, Report)
   │ business rules and transaction boundary
   ▼
DAOs + SQLQueryLoader
   │ PreparedStatement / JDBC
   ▼
MySQL InnoDB (supermarket_db)
   ├─ users, prod opucts, customers, categories
   ├─ bills, bill_items, returns
   ├─ suppliers, batches, stock_transactions, discounts
   └─ audit_log, views, procedures, functions, triggers

After commit: InvoiceGenerator writes a local TXT invoice.


The database is the system of record for financial and stock state. A bill commit does not depend on invoice-file success; invoice failure is reported after the transaction is already committed.

## 2. Complete End-to-End Workflow

### Application entry and authentication

1. `Main.main()` creates `LoginMenu` and enters its loop.
2. The user chooses **Staff Login**, **General User**, or **Exit**.
3. Staff login validates non-empty username/password at the console boundary.
4. `AuthService` hashes the password with SHA-256 and calls `UserDAO.login` using a prepared statement.
5. `UserDAO` returns the matching user, role, and active flag.
6. The UI rejects a null or inactive user and routes `ADMIN`, `BILLER`, and `INVENTORY` to their role menu.

**Security boundary:** in the current console design, role routing is performed in Java menus. Database methods do not receive a server-side authorization context, so production authorization must be enforced in service methods and/or a trusted API boundary as well.

### Bill creation journey

1. A biller opens the billing menu and searches a product by barcode or product identifier.
2. The product record supplies description, current price, GST rate, active status, and stock information.
3. The biller optionally looks up a customer by card ID or leaves the bill as `WALK-IN`.
4. Each item is added to an in-memory list. `BillingService.addItem` rejects non-positive quantities.
5. `BillingService.calculateTotal` sums line totals and tax, rounds monetary values to two decimals, validates that discount is within subtotal, and computes grand total.
6. The payment amount is validated. Cash produces change; UPI/card store the grand total as the paid amount and may carry a transaction reference.
7. `BillingService.saveBill` opens a JDBC connection and sets `autoCommit=false`.
8. `BillDAO.createBill` inserts the bill header and retrieves the generated `bill_id`.
9. For every item, the DAO inserts `bill_items`, then executes a guarded stock update:

   ```sql
   UPDATE products
   SET stock_qty = stock_qty - ?
   WHERE product_id = ? AND active = TRUE AND stock_qty >= ?
   ```

   An update count other than one raises an error. A sale row is also inserted into `stock_transactions`.
10. The transaction commits only after all items succeed. Any exception triggers rollback and the connection is closed.
11. After commit, `InvoiceGenerator` creates `invoices/<bill_no>.txt`. If file generation fails, the bill remains saved and the error is surfaced to the operator.

### Data flow / sequence

```text
Biller
  │ input
  ▼
BillingMenu ──> BillingService.prepareBill/calculateTotal
  │                      │
  │                      └─ validates quantities, discount, payment
  ▼
BillingService.saveBill ── BEGIN / autoCommit=false
  │
  ├─ BillDAO.createBill ── INSERT bills ──> MySQL
  ├─ BillDAO.addBillItem ─ INSERT bill_items ─> MySQL
  ├─ BillDAO.deductStock ─ conditional UPDATE products
  │                         + INSERT stock_transactions
  ├─ MySQL AFTER INSERT trigger ─ INSERT audit_log
  ├─ COMMIT, or ROLLBACK on any error
  └─ InvoiceGenerator ──> local TXT file after commit
```

### Other operational journeys

- **Catalog:** administrator validates product fields, then DAO inserts/updates or deactivates products; uniqueness and non-negative checks are enforced in MySQL.
- **Receiving:** inventory management inserts a product batch, increments product stock, records a `PURCHASE` stock transaction, and commits all three operations together.
- **Returns:** management reads the sold bill item, rejects a return larger than the sold quantity, inserts a return, increments stock, records a `RETURN` transaction, and commits.
- **Reporting:** report DAOs query daily sales, biller sales, low stock, and audit records; SQL views/procedures provide reusable database reporting examples.

## 3. Feature Breakdown & Application Mechanics

### Core feature inventory

1. **Authentication:** active staff lookup by username and SHA-256 password hash.
2. **Role menus:** administrator, biller, inventory, and general-user entry points.
3. **Product management:** add, update, search, list, deactivate, restock, and category management.
4. **Customer management:** card-ID-based registration, update, lookup, list, and history.
5. **Billing:** multiple items, GST/tax totals, fixed discount value, cash/card/UPI enum, transaction reference, change calculation, and bill history.
6. **Inventory:** suppliers, product batches, expiry-oriented batch views, purchase receipts, stock movements, and low-stock reporting.
7. **Returns:** item-level return records with refund amount and stock restoration.
8. **Invoices:** formatted local text files containing bill, customer, item, tax, payment, and change information.
9. **Auditability:** database audit trigger records bill creation; stock transaction rows record purchase, sale, and return movements.
10. **Reports:** daily sales, customer purchase history, biller history, biller sales, low stock, audit logs, and system summary.

### State management

The application is stateful only within one console process. The current bill is held as an in-memory `List<BillItem>` until saved. Authenticated `AppUser` is passed to menu objects. Persistent state belongs in MySQL. There is no distributed session, cache, event bus, or background job.

For a production service, replace mutable floating-point money calculations with `BigDecimal` plus a currency/rounding policy, assign an idempotency key to each bill submission, and keep authoritative totals derived from server-side product prices and tax rules rather than accepting client-calculated amounts.

### Authentication and authorization

Implemented: prepared login query, active-user check, enum-backed roles, and role-specific menu routing.

Required hardening:

- Store password hashes using Argon2id or bcrypt with a unique salt; never use raw SHA-256 for passwords.
- Use `Console.readPassword()` or a compatible no-echo input mechanism.
- Remove credentials from `src/main/resources/db.properties`; inject secrets from environment or a secret manager.
- Add login throttling/lockout and generic failure responses.
- Enforce authorization in service/API methods, not only by hiding console menu options.
- Record authenticated actor IDs on all stock and administrative writes.

### Error handling

Input utilities repeat on invalid numbers and reject empty required strings. Services raise `IllegalArgumentException` for validation failures. JDBC failures bubble to menu-level exception handling. Billing rolls back the database transaction, while invoice generation is deliberately handled as a post-commit warning.

Production error handling should classify validation, authorization, conflict, infrastructure, and unknown failures; log a correlation ID and safe diagnostic context; never expose SQL, credentials, stack traces, or sensitive data to operators or clients; and retry only transient, idempotent operations.

## 4. External Edge Cases & Adversarial Scenarios

1. **Two billers sell the last units of the same product at the same time. Can stock go negative or can both bills succeed?**
2. **A user double-submits a bill after a slow terminal response. Can two bills, two stock deductions, and two invoices be created?**
3. **Can a biller alter `biller_id`, price, discount, tax, role, or another customer ID by crafting a malformed or replayed payload?**
4. **What happens if the database disconnects after the bill header insert, during an item insert, or immediately after commit?**
5. **What happens if invoice generation fails, the disk is full, or an attacker uses a bill number/path value to escape the invoice directory?**
6. **Can a user brute-force credentials, enumerate customer history, or access administrator operations by navigating directly to a menu/service call?**
7. **What happens with huge quantities, repeated identical product lines, extreme decimals, oversized notes, stale product prices, or a long-running report?**

## 5. Answers & Mitigations

### 1. Concurrent last-stock sale

**Current control:** the conditional `UPDATE ... stock_qty >= quantity` is atomic in InnoDB. Each sale checks the affected-row count, and the surrounding transaction rolls back on failure. This is the correct optimistic atomic decrement for stock quantity.

**Required strengthening:** lock and validate all product rows in a deterministic product-ID order when a bill contains multiple products, or retain the guarded update with deadlock retry. Add an integration test with concurrent transactions asserting `stock_qty >= 0`, exactly one success when stock is one, and no orphan bill after failure. Do not rely on the validation trigger alone; it only validates the bill-item insert and does not perform the stock decrement.

### 2. Double-submit / duplicate billing

**Current gap:** `bill_no` is unique, but the current UI can generate a different bill number on two submissions, so uniqueness alone does not guarantee idempotency.

**Mitigation:** generate a client submission UUID once per checkout and persist it in a unique `idempotency_key` column. At the start of the transaction, insert the key or atomically return the existing bill for that key. Keep the unique constraint on `bill_no`, and make invoice generation keyed by the committed bill ID with atomic temp-file-then-move semantics. A retry after an uncertain network/database result first queries the idempotency key before creating anything.

### 3. Tampered totals or identities

**Current control:** most DAO writes use `PreparedStatement`, and schema foreign keys/checks constrain values. The current desktop process is trusted locally, but totals and actor IDs are still supplied by application objects.

**Mitigation:** treat submitted product IDs, quantities, customer ID, payment reference, and idempotency key as input; derive price/GST/tax/discount eligibility and biller identity server-side from current records and the authenticated principal. Recalculate all totals with `BigDecimal`. Add explicit role checks at every mutating service method. Reject unknown fields and enforce maximum lengths before persistence. Use database accounts with least privilege so the application cannot alter schema or grant roles.

### 4. Database disconnect or partial failure

**Current control:** `BillingService.saveBill` uses one connection, disables auto-commit, commits only after all writes, and rolls back when an exception is observed.

**Mitigation:** use a connection pool with validation queries and bounded timeouts. Treat a connection failure before commit as failed and retry only with the same idempotency key. If the failure occurs after commit but before the client receives the response, query the idempotency key/bill number to determine the outcome. Configure deadlock/lock-timeout retry with jitter and a small limit. Add startup health checks and operator-visible database availability status.

### 5. Invoice failure, disk exhaustion, and path traversal

**Current control:** invoice output is based on the generated bill number and failures do not roll back a committed bill.

**Mitigation:** generate filenames from a server-generated safe identifier, not arbitrary user input; enforce a strict allowlist such as `BILL-[0-9A-Z-]+`; resolve and verify the normalized path remains under the configured invoice directory. Write to a temporary file, flush/close, then move atomically. Monitor disk capacity and use a durable object store or print queue in multi-instance deployments. Store an `invoice_status` and retry metadata so a committed bill with a missing invoice is recoverable.

### 6. Brute force, enumeration, and authorization bypass

**Current gaps:** no rate limiter, lockout, session expiry, MFA, or no-echo password input is present; the role check is primarily menu routing.

**Mitigation:** use Argon2id/bcrypt; throttle by account and source with exponential backoff; return the same message for unknown user/inactive user/wrong password; audit login success/failure and administrative actions; expire sessions and require re-authentication for sensitive operations. In an API version, authorize every endpoint with a policy such as `ADMIN`, `BILLER`, or `INVENTORY`; in the console version, make service methods require an immutable authenticated context and test unauthorized calls directly.

### 7. Large, stale, or numerically hostile input

**Current control:** quantity and non-negative numeric input are validated in several services; database checks cap decimal precision and reject invalid signs.

**Mitigation:** cap bill line count, quantity, string lengths, report date ranges, result sizes, and request/console input length. Reject `NaN`, infinity, overflow, and values that cannot fit database precision. Coalesce duplicate product lines before calculation. Re-read authoritative product price/GST/active/stock inside the billing transaction, and fail with a conflict if a catalog version changed. Use pagination, indexes, query timeouts, and bounded report exports. Add resource cleanup and pool metrics to detect leaks.

## 6. Setup & Deployment Guide

### Prerequisites

- JDK 21 (the Maven compiler target is 21; update either the POM or the README if standardizing on 17).
- Maven 3.9+.
- MySQL 8.x with permission to create `supermarket_db`, tables, routines, views, and triggers.
- A UTF-8 capable terminal.

### Database setup

1. Create a dedicated MySQL schema/user for the application; do not use the `root` account.
2. Run `sql/16_master_script/setup_database.sql` against MySQL 8.x. The README currently points to `sql/15_master_script/setup_database.sql`, which does not match the repository tree.
3. Apply any separate SQL scripts in `sql/08_views` through `sql/15_cursors` only when the target environment needs those objects and has reviewed their idempotency.
4. Replace the sample seed credentials immediately. The setup script contains demo users and passwords intended for development only.
5. Verify InnoDB, foreign keys, triggers, and the database timezone/charset.

### Configuration

The current code loads `db.properties` from the classpath and expects `db.url`, `db.username`, `db.password`, and optionally `db.driver`. For development, configure a local file outside version control or a filtered resource. For production, change `DBConnection` to read a secret manager/environment-backed configuration and fail closed when required values are absent.

Minimum configuration contract:

```text
DB_URL=jdbc:mysql://db-host:3306/supermarket_db?useSSL=true&serverTimezone=Asia/Kolkata
DB_USERNAME=smart_billing_app
DB_PASSWORD=<secret-manager-value>
INVOICE_DIRECTORY=<durable writable directory>
```

Do not commit the current password-bearing `src/main/resources/db.properties`. Rotate that credential if it has ever been shared or pushed to a remote repository.

### Local installation and execution

```powershell
mvn clean package
mvn exec:java -Dexec.mainClass=com.supermarket.Main
```

The POM currently does not declare `exec-maven-plugin`; if the second command is unavailable, run the packaged/classes output with the MySQL Connector/J dependency on the classpath, or add a reviewed Maven exec/shade configuration. Confirm that the SQL directory is available from the process working directory because `SQLQueryLoader` reads `Path.of("sql")` directly from the filesystem.

### Deployment pipeline overview

Recommended production pipeline:

1. **Source checks:** compile with the pinned JDK, run unit/integration tests, static analysis, dependency and secret scanning, and fail on committed credentials.
2. **Database migration stage:** version schema changes; run migrations against an ephemeral MySQL instance; verify rollback/forward compatibility.
3. **Artifact stage:** build an immutable JAR/container, generate an SBOM, sign the artifact, and publish it to a private registry.
4. **Pre-production:** inject secrets at runtime, run smoke tests for login, billing, rollback, concurrent stock sales, returns, reports, and invoice recovery.
5. **Production release:** use a least-privilege database user, TLS to MySQL, structured logs/metrics, backups, alerting, and controlled rollout.
6. **Operations:** monitor connection-pool saturation, transaction latency, deadlocks, failed logins, low stock, invoice failures, disk usage, and database backup health. Test restore procedures regularly.

## Repository reference map

- Application entry point: `src/main/java/com/supermarket/Main.java`
- Database connection: `src/main/java/com/supermarket/config/DBConnection.java`
- Billing transaction: `src/main/java/com/supermarket/service/BillingService.java`
- Billing persistence and guarded stock decrement: `src/main/java/com/supermarket/dao/BillDAO.java`
- Authentication: `src/main/java/com/supermarket/service/AuthService.java` and `src/main/java/com/supermarket/dao/UserDAO.java`
- Invoice output: `src/main/java/com/supermarket/util/InvoiceGenerator.java`
- Schema bootstrap: `sql/16_master_script/setup_database.sql`
- Modular SQL examples: `sql/01_database` through `sql/17_migrations`
