# Java Subject Submission

The Java application portion is maintained in `src/main/java/` and follows this flow:

`ui` -> `service` -> `dao` -> `config/util` -> MySQL

Models and enums are in `model/` and `enums/`. DAO classes contain JDBC mapping and transaction logic only. They do not contain SQL text; every application query is retrieved by name through `SQLQueryLoader` from the `sql/` folder.

Build and verify with:

```text
mvn clean test
```

The database must be configured in `src/main/resources/db.properties` before running the console application.
