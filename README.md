# SuperMarket Billing System

Java 17 + JDBC + MySQL 8 console application for a college Java + DBMS project.

## Features

- Admin and Biller login
- Product and stock management
- Customer registration using Card ID
- Cash billing with multiple items
- Transactional bill creation
- Automatic stock reduction through MySQL triggers
- TXT invoice generation
- Customer and biller history
- Daily sales and low-stock reports
- Audit logging
- SQL examples for joins, aggregates, subqueries, views, procedures, triggers, cursor, transactions and indexes

## Project Structure

The `sql/` folder contains the complete DBMS implementation and SQL queries.

The `src/main/java/` folder contains the Java application.

For subject-wise submission, see `DBMS_SUBMISSION.md` and `JAVA_SUBMISSION.md`.
All application SQL is stored in the SQL folder and loaded by `SQLQueryLoader`; DAO classes contain no inline SQL.

## Requirements

- JDK 17+
- Maven
- MySQL 8+

## Database Setup

Run:

```text
sql/15_master_script/setup_database.sql
