package com.supermarket.ui;


import com.supermarket.model.AppUser;
import com.supermarket.model.Product;
import com.supermarket.service.BillingService;
import com.supermarket.service.CustomerService;
import com.supermarket.service.HistoryService;
import com.supermarket.service.ProductService;
import com.supermarket.service.ReportService;
import com.supermarket.util.ConsoleUtil;
import com.supermarket.util.InputUtil;
import com.supermarket.service.ManagementService;

public class BillerMenu {

    private final AppUser user;
    private final ProductService products = new ProductService();
    private final CustomerService customers = new CustomerService();
    private final BillingService billing = new BillingService();
    private final HistoryService history = new HistoryService();
    private final ManagementService management = new ManagementService();

    public BillerMenu(AppUser user) {
        this.user = user;
    }

    public void show() {

        int choice = 0;

        do {
            ConsoleUtil.panelTop("BILLER DASHBOARD");
            ConsoleUtil.panelLine("Welcome, " + user.getFullName(), ConsoleUtil.RESET);
            ConsoleUtil.panelLine("User ID : " + user.getUserId(), ConsoleUtil.RESET);
            ConsoleUtil.panelLine("Role    : " + user.getRole(), ConsoleUtil.YELLOW);
            ConsoleUtil.panelDivider();
            ConsoleUtil.panelMenu(1, "Create New Bill");
            ConsoleUtil.panelMenu(2, "Search Products");
            ConsoleUtil.panelMenu(3, "View Bill Details");
            ConsoleUtil.panelMenu(4, "View Previous Bills");
            ConsoleUtil.panelMenu(5, "Customer Management");
            ConsoleUtil.panelMenu(6, "Check Product Availability");
            ConsoleUtil.panelMenu(7, "View Discounts");
            ConsoleUtil.panelMenu(8, "Print / Save Invoice");
            ConsoleUtil.panelMenu(9, "My Sales Summary");
            ConsoleUtil.panelMenu(10, "Returns / Refunds");
            ConsoleUtil.panelMenu(11, "Logout");
            ConsoleUtil.panelBottom();
            choice = InputUtil.readInt("Enter your choice: ");

            try {
            switch (choice) {

                case 1:
                    new BillingMenu(billing, products, customers).createBill(user);
                    break;

                case 2:
                    searchProducts();
                    break;

                case 3:
                    showBillDetails();
                    break;

                case 4:
                    for (String[] row : history.getBillerHistory(user.getUserId()))
                        System.out.printf("%-22s %-20s %-20s %12s%n", row[0], row[1], row[2], row[3]);
                    break;

                case 5:
                    new CustomerMenu(history, customers).manage();
                    break;

                case 6:
                    searchProducts();
                    break;

                case 7:
                    for (var discount : management.discounts()) System.out.println(discount);
                    break;

                case 8:
                    showBillDetails();
                    break;

                case 9:
                    new ReportMenu(new ReportService()).show();
                    break;

                case 10:
                    String returnBill=InputUtil.readRequired("Bill number: ");
                    var returnBillData=billing.getBillByNo(returnBill);
                    if(returnBillData==null){ConsoleUtil.error("Bill not found.");break;}
                    var returnItems=billing.getItems(returnBillData.getBillId());
                    for(var item:returnItems) System.out.printf("Item ID: %-5d Product: %-25s Sold Qty: %d%n",item.getBillItemId(),item.getProductName(),item.getQuantity());
                    int itemId=InputUtil.readPositiveInt("Bill item ID: ");
                    int qty=InputUtil.readPositiveInt("Return quantity: ");
                    management.returnItem(itemId,user.getUserId(),qty,InputUtil.readLine("Reason: "));
                    ConsoleUtil.success("Return recorded and stock restored.");
                    break;

                case 11:
                    System.out.println(
                        "Logging out from Biller Dashboard..."
                    );
                    break;

                default:
                    ConsoleUtil.error("Invalid choice! Please select 1-11.");
            }
            } catch (Exception e) {
                ConsoleUtil.error("Operation failed: " + e.getMessage());
            }

        } while (choice != 11);
    }

    private void searchProducts() throws Exception {
        String text = InputUtil.readRequired("Barcode or product name: ");
        for (Product product : products.search(text)) System.out.println(product);
    }

    private void showBillDetails() throws Exception {
        String number=InputUtil.readRequired("Bill number or ID: ");
        var bill = billing.getBillByNo(number);
        if (bill==null && number.matches("\\d+")) bill=billing.getBillById(Integer.parseInt(number));
        if (bill == null) { ConsoleUtil.error("Bill not found."); return; }
        System.out.println("Bill " + bill.getBillNo() + " | Total " + ConsoleUtil.money(bill.getGrandTotal()) + " | " + bill.getPaymentMode());
        billing.getItems(bill.getBillId()).forEach(item -> System.out.printf("%-25s %4d %10.2f%n", item.getProductName(), item.getQuantity(), item.getLineTotal()));
    }
}
