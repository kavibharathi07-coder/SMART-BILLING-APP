package com.supermarket.util;

import com.supermarket.model.Bill;
import com.supermarket.model.BillItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class InvoiceGenerator {

    private InvoiceGenerator() {
    }

    public static void generate(
            Bill bill,
            List<BillItem> items
    ) throws IOException {

        File directory =
                new File("invoices");

        if (
                !directory.exists()
                        && !directory.mkdirs()
        ) {

            throw new IOException(
                    "Unable to create invoices directory"
            );
        }

        String fileName =
                "invoices/"
                        + bill.getBillNo()
                        + ".txt";

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd-MM-yyyy hh:mm a"
                );

        try (
                FileWriter writer =
                        new FileWriter(fileName)
        ) {

            writer.write(
                    "============================================================\n"
            );

            writer.write(
                    "                      ABC SUPERMARKET\n"
            );

            writer.write(
                    "============================================================\n"
            );

            writer.write(
                    "Bill No       : "
                            + bill.getBillNo()
                            + "\n"
            );

            writer.write(
                    "Date          : "
                            + (
                            bill.getCreatedAt() == null
                                    ? ""
                                    : bill.getCreatedAt()
                                      .format(formatter)
                    )
                            + "\n"
            );

            writer.write(
                    "Biller        : "
                            + nullToDash(
                                    bill.getBillerName()
                            )
                            + "\n"
            );

            writer.write(
                    "Customer Card : "
                            + (
                            bill.getCustomerCardId()
                                    == null
                                    ? "WALK-IN"
                                    : bill.getCustomerCardId()
                    )
                            + "\n"
            );

            writer.write(
                    "Customer      : "
                            + (
                            bill.getCustomerName()
                                    == null
                                    ? "WALK-IN"
                                    : bill.getCustomerName()
                    )
                            + "\n"
            );

            writer.write(
                    "------------------------------------------------------------\n"
            );

            writer.write(
                    String.format(
                            "%-24s %5s %10s %12s%n",
                            "Product",
                            "Qty",
                            "Price",
                            "Amount"
                    )
            );

            writer.write(
                    "------------------------------------------------------------\n"
            );

            for (BillItem item : items) {

                writer.write(
                        String.format(
                                "%-24s %5d %10.2f %12.2f%n",
                                truncate(
                                        item.getProductName(),
                                        24
                                ),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getLineTotal()
                        )
                );
            }

            writer.write(
                    "------------------------------------------------------------\n"
            );

            writer.write(
                    String.format(
                            "%-45s %12.2f%n",
                            "Subtotal",
                            bill.getSubtotal()
                    )
            );

            writer.write(
                    String.format(
                            "%-45s %12.2f%n",
                            "Discount",
                            bill.getDiscount()
                    )
            );

            writer.write(
                    String.format(
                            "%-45s %12.2f%n",
                            "Tax",
                            bill.getTaxTotal()
                    )
            );

            writer.write(
                    "------------------------------------------------------------\n"
            );

            writer.write(
                    String.format(
                            "%-45s %12.2f%n",
                            "TOTAL",
                            bill.getGrandTotal()
                    )
            );

            writer.write(
                    "------------------------------------------------------------\n"
            );

            writer.write(
                    String.format(
                            "%-45s %12.2f%n",
                            "Cash Received",
                            bill.getCashReceived()
                    )
            );

            writer.write(
                    String.format(
                            "%-45s %12.2f%n",
                            "Change",
                            bill.getChangeAmount()
                    )
            );

            writer.write(
                    "Payment Mode : "
                            + bill.getPaymentMode()
                            + "\n"
            );
            writer.write("Transaction  : " + nullToDash(bill.getTransactionRef()) + "\n");

            writer.write(
                    "------------------------------------------------------------\n"
            );

            writer.write(
                    "                 THANK YOU FOR SHOPPING!\n"
            );

            writer.write(
                    "============================================================\n"
            );
        }
    }

    private static String nullToDash(
            String value
    ) {

        return value == null
                ? "-"
                : value;
    }

    private static String truncate(
            String value,
            int length
    ) {

        if (value == null) {
            return "";
        }

        return value.length() <= length
                ? value
                : value.substring(
                          0,
                          length - 1
                  )
                  + "…";
    }
}
