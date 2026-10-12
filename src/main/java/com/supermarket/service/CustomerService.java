
package com.supermarket.service;

import com.supermarket.dao.CustomerDAO;
import com.supermarket.model.Customer;

import java.util.List;

public class CustomerService {

    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BOLD = "\u001B[1m";

    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    public Customer findCustomer(String customerId) throws Exception {
        logWithColor(CYAN,
                "Searching for customer ID: " + customerId);

        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be empty.");
        }

        try {
            Customer customer = customerDAO.getCustomerById(customerId);

            if (customer != null) {
                logWithColor(GREEN, "Customer found successfully.");
            } else {
                logWithColor(YELLOW, "Customer not found.");
            }

            return customer;

        } catch (Exception e) {
            logWithColor(RED,
                    "Error fetching customer: " + e.getMessage());
            throw e;
        }
    }

    public List<String> getCustomerHistory(String customerId)
            throws Exception {

        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be empty.");
        }

        try {
            List<String> history =
                    customerDAO.getCustomerHistory(customerId);

            logWithColor(GREEN,
                    "Retrieved " + history.size() + " history records.");

            return history;

        } catch (Exception e) {
            logWithColor(RED,
                    "Error fetching customer history: " + e.getMessage());
            throw e;
        }
    }

    public List<Customer> getAllCustomers() throws Exception {
        try {
            return customerDAO.getAllCustomers();
        } catch (Exception e) {
            logWithColor(RED,
                    "Error fetching customer list: " + e.getMessage());
            throw e;
        }
    }

    public void addCustomer(Customer customer) throws Exception {
        if (customer.getCardId()==null || customer.getCardId().isBlank() || customer.getCustomerName()==null || customer.getCustomerName().isBlank()) throw new IllegalArgumentException("Card ID and customer name are required");
        customerDAO.addCustomer(customer);
    }

    public void updateCustomer(Customer customer) throws Exception { customerDAO.updateCustomer(customer); }

    private void logWithColor(String colorCode, String message) {
        System.out.println(BOLD + colorCode + message + RESET);
    }
}
