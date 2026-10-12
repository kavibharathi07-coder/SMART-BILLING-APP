package com.supermarket.ui;

import com.supermarket.model.Customer;
import com.supermarket.service.CustomerService;
import com.supermarket.util.InputUtil;

import java.util.List;

public class GeneralUserMenu {

    private final CustomerService customerService;

    public GeneralUserMenu() {
        this.customerService = new CustomerService();
    }

    public void show() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("           GENERAL USER LOGIN");
        System.out.println("==========================================");

        String cardId = readCardId();

        try {

            Customer customer =
                    customerService.findCustomer(
                            cardId
                    );

            if (customer == null) {

                System.out.println();
                System.out.println(
                        "CUSTOMER NOT FOUND"
                );

                System.out.println(
                        "No customer exists with ID: "
                                + cardId
                );

                return;
            }

            System.out.println();
            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "       CUSTOMER LOGIN SUCCESSFUL"
            );

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "Customer ID : "
                            + customer.getCustomerId()
            );

            System.out.println(
                    "Card ID     : "
                            + customer.getCardId()
            );

            System.out.println(
                    "Name        : "
                            + customer.getCustomerName()
            );

            System.out.println(
                    "Phone       : "
                            + customer.getPhone()
            );

            System.out.println(
                    "Email       : "
                            + customer.getEmail()
            );

            showHistory(cardId);

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "Unable to load customer details."
            );

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }

    private String readCardId() {

        while (true) {

            String input =
                    InputUtil.readRequired(
                            "Enter Customer Card ID: "
                    );
            return input;
        }
    }

    private void showHistory(String cardId)
            throws Exception {

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "          PURCHASE HISTORY"
        );

        System.out.println(
                "=========================================="
        );

        List<String> history =
                customerService.getCustomerHistory(
                        cardId
                );

        if (history.isEmpty()) {

            System.out.println(
                    "No purchase history found."
            );

            return;
        }

        System.out.printf(
                "%-5s %-20s %-20s %-12s %-12s %-12s %-12s %-10s%n",
                "ID",
                "BILL NO",
                "DATE",
                "SUBTOTAL",
                "DISCOUNT",
                "TAX",
                "TOTAL",
                "PAYMENT"
        );

        System.out.println(
                "-------------------------------------------------------------------------------------------------------------"
        );

        for (String record : history) {
            System.out.println(record);
        }

        System.out.println(
                "-------------------------------------------------------------------------------------------------------------"
        );
    }
}