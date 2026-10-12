package com.supermarket.ui;


import com.supermarket.model.AppUser;
import com.supermarket.service.ProductService;
import com.supermarket.service.ManagementService;
import com.supermarket.enums.Role;
import com.supermarket.util.InputUtil;
import com.supermarket.util.ConsoleUtil;

public class AdminMenu {

    private final AppUser user;
    private final ManagementService management = new ManagementService();

    public AdminMenu(AppUser user) {
        this.user = user;
    }

    public void show() throws Exception {

        int choice = 0;

        do {
            ConsoleUtil.panelTop("ADMIN DASHBOARD");
            ConsoleUtil.panelLine("Welcome, " + user.getFullName(), ConsoleUtil.RESET);
            ConsoleUtil.panelLine("User ID : " + user.getUserId(), ConsoleUtil.RESET);
            ConsoleUtil.panelLine("Role    : " + user.getRole(), ConsoleUtil.YELLOW);
            ConsoleUtil.panelDivider();
            ConsoleUtil.panelMenu(1, "Product Management");
            ConsoleUtil.panelMenu(2, "Category Management");
            ConsoleUtil.panelMenu(3, "Supplier Management");
            ConsoleUtil.panelMenu(4, "Inventory Management");
            ConsoleUtil.panelMenu(5, "Employee Management");
            ConsoleUtil.panelMenu(6, "Discount Management");
            ConsoleUtil.panelMenu(7, "Sales Reports");
            ConsoleUtil.panelMenu(8, "Customer Management");
            ConsoleUtil.panelMenu(9, "Audit Logs");
            ConsoleUtil.panelMenu(10, "System Summary");
            ConsoleUtil.panelMenu(11, "Logout");
            ConsoleUtil.panelBottom();

            choice = InputUtil.readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    ProductService productService = new ProductService();
                    ProductMenu productMenu = new ProductMenu(productService);
                    productMenu.show();
                    break;

                case 2:
                    for (var category : new ProductService().getCategories()) System.out.println(category);
                    String category = com.supermarket.util.InputUtil.readLine("New category name (ENTER to keep list): ");
                    if (!category.isBlank()) new ProductService().addCategory(category);
                    break;

                case 3:
                    suppliers();
                    break;

                case 4:
                    new ProductMenu(new ProductService()).show();
                    break;

                case 5:
                    employees();
                    break;

                case 6:
                    discounts();
                    break;

                case 7:
                    new ReportMenu(new com.supermarket.service.ReportService()).show();
                    break;

                case 8:
                    new CustomerMenu(new com.supermarket.service.HistoryService(), new com.supermarket.service.CustomerService()).manage();
                    break;

                case 9:
                    new ReportMenu(new com.supermarket.service.ReportService()).showAuditLogs();
                    break;

                case 10:
                    java.util.Map<String,Object> summary = new com.supermarket.service.ReportService().dailySales(java.time.LocalDate.now());
                    System.out.println("Today's bills: " + summary.get("total_bills") + ", sales: " + summary.get("total_sales"));
                    break;

                case 11:
                    System.out.println("Logging out from Admin Dashboard...");
                    break;

                default:
                    System.out.println(
                        "Invalid choice! Please select 1-11."
                    );
            }

        } while(choice!= 11);
    }

    private void suppliers() throws Exception {
        System.out.println("ID  NAME                         CONTACT                 PHONE");
        for (var r : management.suppliers()) System.out.printf("%-3s %-28s %-22s %-15s%n", r.get("supplier_id"), r.get("supplier_name"), r.get("contact_person"), r.get("phone"));
        String name=InputUtil.readLine("Supplier name (ENTER to keep list): ");
        if (!name.isBlank()) management.addSupplier(name,InputUtil.readLine("Contact person: "),InputUtil.readLine("Phone: "),InputUtil.readLine("Email: "));
    }

    private void employees() throws Exception {
        for (var r : management.employees()) System.out.printf("%-4s %-15s %-22s %-10s %-6s%n",r.get("user_id"),r.get("username"),r.get("full_name"),r.get("role"),r.get("active"));
        if (InputUtil.readLine("Add employee? (y/N): ").equalsIgnoreCase("y")) {
            String role=InputUtil.readRequired("Role (BILLER/INVENTORY): ").toUpperCase();
            management.addEmployee(InputUtil.readRequired("Username: "),InputUtil.readRequired("Password: "),InputUtil.readRequired("Full name: "),Role.valueOf(role));
        }
        String deactivate=InputUtil.readLine("Deactivate employee ID (ENTER to skip): ");
        if (!deactivate.isBlank()) management.deactivateEmployee(Integer.parseInt(deactivate));
    }

    private void discounts() throws Exception {
        for (var r : management.discounts()) System.out.printf("%-4s %-12s %-8s %-10s %-6s to %-6s %-6s%n",r.get("discount_id"),r.get("code"),r.get("discount_type"),r.get("value"),r.get("start_date"),r.get("end_date"),r.get("active"));
        if (InputUtil.readLine("Add discount? (y/N): ").equalsIgnoreCase("y")) {
            String type=InputUtil.readRequired("Type (PERCENT/FIXED): ").toUpperCase();
            management.addDiscount(InputUtil.readRequired("Code: "),InputUtil.readLine("Description: "),type,InputUtil.readNonNegativeDouble("Value: "),InputUtil.readNonNegativeDouble("Maximum amount (0=none): "),java.time.LocalDate.parse(InputUtil.readRequired("Start date (YYYY-MM-DD): ")),java.time.LocalDate.parse(InputUtil.readRequired("End date (YYYY-MM-DD): ")));
        }
        String deactivate=InputUtil.readLine("Deactivate discount ID (ENTER to skip): ");
        if (!deactivate.isBlank()) management.deactivateDiscount(Integer.parseInt(deactivate));
    }
}
