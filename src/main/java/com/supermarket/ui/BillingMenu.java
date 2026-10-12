package com.supermarket.ui;

import com.supermarket.model.AppUser;
import com.supermarket.model.Bill;
import com.supermarket.model.BillItem;
import com.supermarket.model.Customer;
import com.supermarket.model.Product;
import com.supermarket.service.BillingService;
import com.supermarket.service.CustomerService;
import com.supermarket.service.ProductService;
import com.supermarket.util.ConsoleUtil;
import com.supermarket.util.InputUtil;
import com.supermarket.enums.PaymentMode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BillingMenu {

    private final BillingService billingService;

    private final ProductService productService;

    private final CustomerService customerService;

    public BillingMenu(
            BillingService billingService,
            ProductService productService,
            CustomerService customerService
    ) {

        this.billingService =
                billingService;

        this.productService =
                productService;

        this.customerService =
                customerService;
    }

    public void createBill(
            AppUser biller
    ) throws Exception {

        ConsoleUtil.header(
                "CREATE BILL"
        );

        String cardId =
                InputUtil.readLine(
                        "Enter Customer Card ID or press ENTER for Walk-in: "
                );

        Customer customer = null;

        if (!cardId.isBlank()) {

            customer =
                    customerService.findCustomer(
                            cardId
                    );

            if (customer == null) {

                System.out.println(
                        "Customer not found."
                );

                return;
            }

            System.out.println(
                    "Customer: "
                            + customer.getCustomerName()
            );

        } else {

            System.out.println(
                    "Customer: WALK-IN"
            );
        }

        List<BillItem> items =
                new ArrayList<>();

        while (true) {

            System.out.println();

            System.out.println(
                    "1. Add Product"
            );

            System.out.println(
                    "2. Remove Product"
            );

            System.out.println(
                    "3. View Cart"
            );

            System.out.println(
                    "4. Checkout"
            );

            System.out.println(
                    "5. Cancel"
            );

            String choice =
                    InputUtil.readRequired(
                            "Enter choice: "
                    );

            switch (choice) {

                case "1":
                    addItem(items);
                    break;

                case "2":
                    removeItem(items);
                    break;

                case "3":
                    printCart(items);
                    break;

                case "4":

                    if (items.isEmpty()) {

                        System.out.println(
                                "Cart is empty."
                        );

                        continue;
                    }

                    double discount =
                            InputUtil
                                    .readNonNegativeDouble(
                                            "Discount: "
                                    );

                    double[] totals =
                            billingService.calculateTotal(
                                    items,
                                    discount
                            );

                    printTotals(
                            totals[0],
                            discount,
                            totals[1],
                            totals[2]
                    );

                    String paymentChoice = InputUtil.readRequired("Payment mode (1-CASH, 2-UPI, 3-CARD): ");
                    PaymentMode paymentMode = switch (paymentChoice) {
                        case "2" -> PaymentMode.UPI;
                        case "3" -> PaymentMode.CARD;
                        default -> PaymentMode.CASH;
                    };
                    double cash =
                            InputUtil
                                    .readNonNegativeDouble(
                                            paymentMode == PaymentMode.CASH ? "Cash received: " : "Amount paid: "
                                    );
                    String transactionRef = paymentMode == PaymentMode.CASH ? "CASH" : InputUtil.readRequired("Transaction reference: ");

                    String billNo =
                            generateBillNo();

                    Bill bill =
                            billingService.prepareBill(
                                    billNo,
                                    customer == null
                                            ? null
                                            : customer.getCustomerId(),
                                    biller.getUserId(),
                                    items,
                                    discount,
                                    cash, paymentMode, transactionRef
                            );

                    bill.setCustomerCardId(
                            customer == null
                                    ? null
                                    : customer.getCardId()
                    );

                    bill.setCustomerName(
                            customer == null
                                    ? null
                                    : customer.getCustomerName()
                    );

                    bill.setBillerName(
                            biller.getFullName()
                    );

                    bill.setCreatedAt(
                            LocalDateTime.now()
                    );

                    billingService.saveBill(
                            bill,
                            items
                    );

                    ConsoleUtil.success("PAYMENT SUCCESS");

                    System.out.println(
                            "Bill No : "
                                    + bill.getBillNo()
                    );

                    System.out.println(
                            "Change  : "
                                    + ConsoleUtil.money(
                                            bill.getChangeAmount()
                                    )
                    );

                    System.out.println(
                            "Invoice : invoices/"
                                    + bill.getBillNo()
                                    + ".txt"
                    );
                    System.out.println("Payment : " + paymentMode + " / " + transactionRef);

                    return;

                case "5":
                    return;

                default:
                        ConsoleUtil.error("Invalid choice.");
            }
        }
    }

    private void addItem(
            List<BillItem> items
    ) throws Exception {

        String barcode =
                InputUtil.readRequired(
                        "Enter barcode: "
                );

        Product product =
                productService.searchProduct(
                        barcode
                );

        if (product == null
                || !product.isActive()) {

            System.out.println(
                    "Product not found or inactive."
            );

            return;
        }

        System.out.println(
                "Product: "
                        + product.getProductName()
        );

        System.out.println(
                "Price: "
                        + ConsoleUtil.money(
                                product.getPrice()
                        )
        );

        System.out.println(
                "Available Stock: "
                        + product.getStockQty()
        );

        int qty =
                InputUtil.readPositiveInt(
                        "Quantity: "
                );

        if (qty > product.getStockQty()) {

            System.out.println(
                    "Insufficient stock."
            );

            return;
        }

        billingService.addItem(
                items,
                new BillItem(
                        product.getProductId(),
                        product.getProductName(),
                        qty,
                        product.getPrice(),
                        product.getGstRate()
                )
        );

        ConsoleUtil.success("Added successfully.");
    }

    private void removeItem(
            List<BillItem> items
    ) {

        if (items.isEmpty()) {

            System.out.println(
                    "Cart is empty."
            );

            return;
        }

        printCart(items);

        int index =
                InputUtil.readPositiveInt(
                        "Enter item number to remove: "
                ) - 1;

        if (
                index < 0
                        || index >= items.size()
        ) {

            System.out.println(
                    "Invalid item number."
            );

            return;
        }

        items.remove(index);

        System.out.println(
                "Item removed."
        );
    }

    private void printCart(
            List<BillItem> items
    ) {

        ConsoleUtil.separator();

        System.out.printf(
                "%-4s %-24s %5s %10s %12s%n",
                "No",
                "Product",
                "Qty",
                "Price",
                "Amount"
        );

        for (
                int i = 0;
                i < items.size();
                i++
        ) {

            BillItem item =
                    items.get(i);

            System.out.printf(
                    "%-4d %-24s %5d %10.2f %12.2f%n",
                    i + 1,
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getLineTotal()
            );
        }

        ConsoleUtil.separator();
    }

    private void printTotals(
            double subtotal,
            double discount,
            double tax,
            double total
    ) {

        System.out.println(
                "Subtotal : "
                        + ConsoleUtil.money(
                                subtotal
                        )
        );

        System.out.println(
                "Discount : "
                        + ConsoleUtil.money(
                                discount
                        )
        );

        System.out.println(
                "Tax      : "
                        + ConsoleUtil.money(
                                tax
                        )
        );

        System.out.println(
                "TOTAL    : "
                        + ConsoleUtil.money(
                                total
                        )
        );
    }

    private String generateBillNo() {

        String stamp =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMdd-HHmmss"
                                )
                        );

        return "BILL-"
                + stamp
                + "-"
                + (System.currentTimeMillis() % 1000);
    }
}
