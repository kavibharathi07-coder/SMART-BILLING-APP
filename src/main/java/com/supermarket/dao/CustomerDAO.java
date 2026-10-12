
package com.supermarket.dao;

import com.supermarket.config.DBConnection;
import com.supermarket.model.Customer;
import com.supermarket.util.SQLQueryLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private static final String SQL_FILE =
            "sql/04_crud_queries/customer_queries.sql";

    public Customer getCustomerById(String customerId)
            throws Exception {

        String sql = SQLQueryLoader.load(
                SQL_FILE,
                "GET_CUSTOMER_BY_ID"
        );

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Customer customer = new Customer();

                    customer.setCustomerId(
                            resultSet.getInt("customer_id"));
                    customer.setCardId(
                            resultSet.getString("card_id"));
                    customer.setCustomerName(
                            resultSet.getString("customer_name"));
                    customer.setPhone(
                            resultSet.getString("phone"));
                    customer.setEmail(
                            resultSet.getString("email"));

                    return customer;
                }
            }
        }

        return null;
    }

    public List<String> getCustomerHistory(String customerId)
            throws Exception {

        String sql = SQLQueryLoader.load(
                SQL_FILE,
                "CUSTOMER_PURCHASE_HISTORY"
        );

        List<String> history = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    history.add(String.format(
                            "%-5d %-20s %-20s %-12.2f %-12.2f %-12.2f %-12.2f %-10s",
                            resultSet.getInt("bill_id"),
                            resultSet.getString("bill_no"),
                            resultSet.getString("created_at"),
                            resultSet.getDouble("subtotal"),
                            resultSet.getDouble("discount"),
                            resultSet.getDouble("tax_total"),
                            resultSet.getDouble("grand_total"),
                            resultSet.getString("payment_mode")
                    ));
                }
            }
        }

        return history;
    }

    public List<Customer> getAllCustomers() throws Exception {

        String sql = SQLQueryLoader.load(
                SQL_FILE,
                "GET_ALL_CUSTOMERS"
        );

        List<Customer> customers = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Customer customer = new Customer();

                customer.setCustomerId(
                        resultSet.getInt("customer_id"));
                customer.setCardId(
                        resultSet.getString("card_id"));
                customer.setCustomerName(
                        resultSet.getString("customer_name"));
                customer.setPhone(
                        resultSet.getString("phone"));
                customer.setEmail(
                        resultSet.getString("email"));

                customers.add(customer);
            }
        }

        return customers;
    }

    public void addCustomer(Customer customer) throws Exception {
        String sql = SQLQueryLoader.getQuery("CUSTOMER_CREATE");
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getCardId());
            statement.setString(2, customer.getCustomerName());
            statement.setString(3, customer.getPhone());
            statement.setString(4, customer.getEmail());
            statement.executeUpdate();
        }
    }

    public void updateCustomer(Customer customer) throws Exception {
        String sql = SQLQueryLoader.getQuery("CUSTOMER_UPDATE");
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getCustomerName());
            statement.setString(2, customer.getPhone());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getCardId());
            statement.executeUpdate();
        }
    }
}
