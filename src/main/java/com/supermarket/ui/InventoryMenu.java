package com.supermarket.ui;

import com.supermarket.model.AppUser;
import com.supermarket.service.ProductService;
import com.supermarket.service.ReportService;
import com.supermarket.util.ConsoleUtil;
import com.supermarket.util.InputUtil;
import com.supermarket.service.ManagementService;

public class InventoryMenu {
    private final AppUser user;
    private final ProductService products = new ProductService();
    private final ManagementService management = new ManagementService();
    public InventoryMenu(AppUser user) { this.user = user; }
    public void show() {
        while (true) {
            ConsoleUtil.panelTop("INVENTORY DASHBOARD");
            ConsoleUtil.panelLine("Welcome, " + user.getFullName(), ConsoleUtil.RESET);
            ConsoleUtil.panelLine("User ID : " + user.getUserId(), ConsoleUtil.RESET);
            ConsoleUtil.panelLine("Role    : " + user.getRole(), ConsoleUtil.YELLOW);
            ConsoleUtil.panelDivider();
            ConsoleUtil.panelMenu(1, "Receive Stock");
            ConsoleUtil.panelMenu(2, "View Stock");
            ConsoleUtil.panelMenu(3, "Low-stock Alerts");
            ConsoleUtil.panelMenu(4, "Search Product");
            ConsoleUtil.panelMenu(5, "Batches / Expiry");
            ConsoleUtil.panelMenu(6, "Stock History");
            ConsoleUtil.panelMenu(7, "Logout");
            ConsoleUtil.panelBottom();
            String choice = InputUtil.readRequired("Enter choice: ");
            try {
                switch (choice) {
                    case "1":
                        System.out.println("Available products:");
                        products.getAllProducts().forEach(p -> System.out.printf("ID: %-5d %s%n", p.getProductId(), p));
                        System.out.println("Available suppliers:");
                        management.suppliers().forEach(System.out::println);
                        int product=InputUtil.readPositiveInt("Product ID: ");
                        int supplier=InputUtil.readInt("Supplier ID (0=none): ");
                        management.addBatch(product,supplier,InputUtil.readRequired("Batch number: "),java.time.LocalDate.parse(InputUtil.readRequired("Expiry date (YYYY-MM-DD or 2099-12-31): ")),InputUtil.readPositiveInt("Quantity received: "),InputUtil.readNonNegativeDouble("Purchase price: "));
                        ConsoleUtil.success("Stock and batch updated.");
                        break;
                    case "2": products.getAllProducts().forEach(System.out::println); break;
                    case "3": new ReportMenu(new ReportService()).show(); break;
                    case "4": products.search(InputUtil.readRequired("Search: ")).forEach(System.out::println); break;
                    case "5": management.batches(true).forEach(System.out::println); break;
                    case "6": management.stockHistory().forEach(System.out::println); break;
                    case "7": return;
                    default: ConsoleUtil.error("Invalid choice.");
                }
            } catch (Exception e) { ConsoleUtil.error("Operation failed: " + e.getMessage()); }
            ConsoleUtil.pause();
        }
    }
}
