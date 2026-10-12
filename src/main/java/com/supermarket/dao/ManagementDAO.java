package com.supermarket.dao;

import com.supermarket.config.DBConnection;
import com.supermarket.enums.Role;
import com.supermarket.util.PasswordUtil;
import com.supermarket.util.SQLQueryLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ManagementDAO {

    private String query(String name) {
        return SQLQueryLoader.getQuery(name);
    }

    private List<Map<String, Object>> rows(String queryName, Object... values)
            throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query(queryName))) {
            setParameters(statement, values);

            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metadata = resultSet.getMetaData();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int column = 1; column <= metadata.getColumnCount(); column++) {
                        row.put(metadata.getColumnLabel(column), resultSet.getObject(column));
                    }
                    result.add(row);
                }
            }
        }

        return result;
    }

    private void execute(String queryName, Object... values) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query(queryName))) {
            setParameters(statement, values);
            statement.executeUpdate();
        }
    }

    private void setParameters(PreparedStatement statement, Object... values)
            throws Exception {
        for (int index = 0; index < values.length; index++) {
            statement.setObject(index + 1, values[index]);
        }
    }

    public List<Map<String, Object>> suppliers() throws Exception {
        return rows("SUPPLIER_LIST");
    }

    public void addSupplier(String name, String contact, String phone, String email)
            throws Exception {
        execute("SUPPLIER_CREATE", name, contact, phone, email);
    }

    public void deactivateSupplier(int id) throws Exception {
        execute("SUPPLIER_DEACTIVATE", id);
    }

    public void addBatch(int product, int supplier, String batch, LocalDate expiry,
                         int quantity, double purchasePrice) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                execute(connection, "BATCH_CREATE", product, supplier == 0 ? null : supplier,
                        batch, expiry, quantity, quantity, purchasePrice);

                if (updatedRows(connection, "PRODUCT_ADD_STOCK", quantity, product) != 1) {
                    throw new IllegalArgumentException("Product not found");
                }

                execute(connection, "STOCK_RECORD_PURCHASE", product, quantity,
                        "Batch " + batch);
                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<Map<String, Object>> batches(boolean expiring) throws Exception {
        return rows(expiring ? "BATCH_LIST_EXPIRING" : "BATCH_LIST");
    }

    public List<Map<String, Object>> stockHistory() throws Exception {
        return rows("STOCK_HISTORY");
    }

    public List<Map<String, Object>> discounts() throws Exception {
        return rows("DISCOUNT_LIST");
    }

    public void addDiscount(String code, String description, String type, double value,
                            double max, LocalDate start, LocalDate end) throws Exception {
        execute("DISCOUNT_CREATE", code, description, type, value,
                max == 0 ? null : max, start, end);
    }

    public void deactivateDiscount(int id) throws Exception {
        execute("DISCOUNT_DEACTIVATE", id);
    }

    public List<Map<String, Object>> employees() throws Exception {
        return rows("EMPLOYEE_LIST");
    }

    public void addEmployee(String username, String password, String name, Role role)
            throws Exception {
        execute("EMPLOYEE_CREATE", username, PasswordUtil.hashPassword(password),
                name, role.name());
    }

    public void deactivateEmployee(int id) throws Exception {
        execute("EMPLOYEE_DEACTIVATE", id);
    }

    public void returnItem(int billItemId, int userId, int quantity, String reason)
            throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int billId;
                int productId;
                double refundAmount;

                try (PreparedStatement statement = connection.prepareStatement(
                        query("RETURN_FIND_BILL_ITEM"))) {
                    statement.setInt(1, billItemId);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new IllegalArgumentException("Bill item not found");
                        }

                        billId = resultSet.getInt("bill_id");
                        productId = resultSet.getInt("product_id");
                        int soldQuantity = resultSet.getInt("quantity");

                        if (quantity > soldQuantity) {
                            throw new IllegalArgumentException("Return exceeds sold quantity");
                        }

                        refundAmount = resultSet.getDouble("line_total") * quantity / soldQuantity;
                    }
                }

                execute(connection, "RETURN_CREATE", billId, billItemId, productId,
                        quantity, refundAmount, reason, userId);
                execute(connection, "RETURN_ADD_STOCK", quantity, productId);
                execute(connection, "STOCK_RECORD_RETURN", productId, userId,
                        quantity, billItemId, reason);
                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void execute(Connection connection, String queryName, Object... values)
            throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(query(queryName))) {
            setParameters(statement, values);
            statement.executeUpdate();
        }
    }

    private int updatedRows(Connection connection, String queryName, Object... values)
            throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(query(queryName))) {
            setParameters(statement, values);
            return statement.executeUpdate();
        }
    }
}
