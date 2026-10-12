package com.supermarket.ui;

import com.supermarket.model.Customer;
import com.supermarket.service.CustomerService;
import com.supermarket.service.HistoryService;
import com.supermarket.util.ConsoleUtil;
import com.supermarket.util.InputUtil;

public class CustomerMenu {

    private final HistoryService historyService;

    private final CustomerService customerService;

    public CustomerMenu(
            HistoryService historyService,
            CustomerService customerService
    ) {

        this.historyService =
                historyService;

        this.customerService =
                customerService;
    }

    public void showHistoryOnly()
            throws Exception {

        ConsoleUtil.header(
                "CUSTOMER PURCHASE HISTORY"
        );

        String cardId =
                InputUtil.readRequired(
                        "Enter Card ID: "
                );

        if (historyService == null) {

            throw new IllegalStateException(
                    "History service is not available"
            );
        }

        var rows =
                historyService.getCustomerHistory(
                        cardId
                );

        if (rows.isEmpty()) {

            System.out.println(
                    "No purchase history found."
            );

            return;
        }

        System.out.printf(
                "%-22s %-20s %-18s %12s%n",
                "BILL NO",
                "DATE",
                "BILLER",
                "AMOUNT"
        );

        for (String[] r : rows) {

            System.out.printf(
                    "%-22s %-20s %-18s %12s%n",
                    r[0],
                    r[1],
                    r[2],
                    ConsoleUtil.money(
                            Double.parseDouble(
                                    r[3]
                            )
                    )
            );
        }
    }

    public void showAdminList()
            throws Exception {

        ConsoleUtil.header(
                "CUSTOMER LIST"
        );

        for (
                Customer c :
                customerService.getAllCustomers()
        ) {

            System.out.println(c);
        }
    }

    public void manage() throws Exception {
        showAdminList();
        String card=InputUtil.readLine("Customer card ID to edit, or ENTER to add: ");
        if (card.isBlank()) {
            Customer c=new Customer();
            c.setCardId(InputUtil.readRequired("Card ID: ")); c.setCustomerName(InputUtil.readRequired("Name: ")); c.setPhone(InputUtil.readLine("Phone: ")); c.setEmail(InputUtil.readLine("Email: "));
            customerService.addCustomer(c); ConsoleUtil.success("Customer added.");
        } else {
            Customer c=customerService.findCustomer(card); if(c==null){ConsoleUtil.error("Customer not found.");return;}
            c.setCustomerName(InputUtil.readRequired("Name: ")); c.setPhone(InputUtil.readLine("Phone: ")); c.setEmail(InputUtil.readLine("Email: ")); customerService.updateCustomer(c); ConsoleUtil.success("Customer updated.");
        }
    }
}
