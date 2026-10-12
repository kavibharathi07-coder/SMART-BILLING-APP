package com.supermarket.service;

import com.supermarket.config.DBConnection;
import com.supermarket.dao.BillDAO;
import com.supermarket.enums.PaymentMode;
import com.supermarket.model.Bill;
import com.supermarket.model.BillItem;
import com.supermarket.util.InvoiceGenerator;

import java.sql.Connection;
import java.util.List;

public class BillingService {

    private final BillDAO billDAO =
            new BillDAO();

    public void addItem(
            List<BillItem> items,
            BillItem item
    ) {

        if (item.getQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be > 0"
            );
        }

        items.add(item);
    }

    public double[] calculateTotal(
            List<BillItem> items,
            double discount
    ) {

        double subtotal = 0;
        double tax = 0;

        for (BillItem item : items) {

            subtotal += item.getLineTotal();
            tax += item.getTaxAmount();
        }

        subtotal = money(subtotal);
        tax = money(tax);

        if (discount < 0 || discount > subtotal) {
            throw new IllegalArgumentException("Discount must be between 0 and the subtotal");
        }

        double grandTotal =
                money(
                        subtotal
                                - discount
                                + tax
                );

        return new double[]{
                subtotal,
                tax,
                grandTotal
        };
    }

    public void validateCash(
            double cashReceived,
            double total
    ) {

        if (cashReceived < total) {

            throw new IllegalArgumentException(
                    "Cash received must be at least the total amount"
            );
        }
    }

    public void validatePayment(double received, double total, PaymentMode mode) {
        if (received < total) throw new IllegalArgumentException("Amount paid must be at least the total amount");
    }

    public void saveBill(
            Bill bill,
            List<BillItem> items
    ) throws Exception {

        if (items == null
                || items.isEmpty()) {

            throw new IllegalArgumentException(
                    "Bill must contain at least one item"
            );
        }

        Connection connection =
                DBConnection.getConnection();

        try {

            connection.setAutoCommit(false);

            int billId =
                    billDAO.createBill(
                            connection,
                            bill
                    );

            bill.setBillId(billId);

            for (BillItem item : items) {
                billDAO.addBillItem(
                        connection,
                        billId,
                        item
                );
                billDAO.deductStock(connection, item);
            }

            connection.commit();

            try {

                InvoiceGenerator.generate(
                        bill,
                        items
                );

            } catch (Exception invoiceError) {

                System.out.println(
                        "Bill saved, but invoice generation failed: "
                                + invoiceError.getMessage()
                );
            }

        } catch (Exception e) {

            try {
                connection.rollback();
            } catch (Exception ignored) {
            }

            throw e;

        } finally {

            try {
                connection.setAutoCommit(true);
            } catch (Exception ignored) {
            }

            connection.close();
        }
    }

    public Bill getBillById(
            int billId
    ) throws Exception {

        return billDAO.getBillById(billId);
    }

    public Bill getBillByNo(
            String billNo
    ) throws Exception {

        return billDAO.getBillByNo(billNo);
    }

    public List<BillItem> getItems(
            int billId
    ) throws Exception {

        return billDAO.getBillItems(billId);
    }

    private double money(
            double value
    ) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }

    public Bill prepareBill(
            String billNo,
            Integer customerId,
            int billerId,
            List<BillItem> items,
            double discount,
            double cashReceived
    ) {

        double[] totals =
                calculateTotal(
                        items,
                        discount
                );

        double subtotal = totals[0];
        double tax = totals[1];
        double grandTotal = totals[2];

        validateCash(
                cashReceived,
                grandTotal
        );

        return new Bill(
                billNo,
                customerId,
                billerId,
                subtotal,
                discount,
                tax,
                grandTotal,
                PaymentMode.CASH,
                cashReceived,
                money(
                        cashReceived
                                - grandTotal
                )
        );
    }

    public Bill prepareBill(String billNo, Integer customerId, int billerId, List<BillItem> items,
                            double discount, double received, PaymentMode mode, String transactionRef) {
        double[] totals=calculateTotal(items,discount);
        validatePayment(received,totals[2],mode);
        Bill bill=new Bill(billNo,customerId,billerId,totals[0],discount,totals[1],totals[2],mode,
                mode==PaymentMode.CASH?received:totals[2],mode==PaymentMode.CASH?money(received-totals[2]):0);
        bill.setTransactionRef(transactionRef);
        return bill;
    }
}
