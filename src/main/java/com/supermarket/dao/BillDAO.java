package com.supermarket.dao;

import com.supermarket.config.DBConnection;
import com.supermarket.enums.PaymentMode;
import com.supermarket.model.Bill;
import com.supermarket.model.BillItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public int createBill(Connection connection, Bill bill) throws Exception {
        String sql = "INSERT INTO bills (bill_no, customer_id, biller_id, subtotal, discount, tax_total, grand_total, payment_mode, cash_received, change_amount, transaction_ref) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bill.getBillNo());
            ps.setObject(2, bill.getCustomerId());
            ps.setInt(3, bill.getBillerId());
            ps.setDouble(4, bill.getSubtotal());
            ps.setDouble(5, bill.getDiscount());
            ps.setDouble(6, bill.getTaxTotal());
            ps.setDouble(7, bill.getGrandTotal());
            ps.setString(8, bill.getPaymentMode() == null ? PaymentMode.CASH.name() : bill.getPaymentMode().name());
            ps.setDouble(9, bill.getCashReceived());
            ps.setDouble(10, bill.getChangeAmount());
            ps.setString(11, bill.getTransactionRef());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new IllegalStateException("Failed to create bill");
    }

    public void addBillItem(Connection connection, int billId, BillItem item) throws Exception {
        String sql = "INSERT INTO bill_items (bill_id, product_id, quantity, unit_price, gst_rate, tax_amount, line_total) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getUnitPrice());
            ps.setDouble(5, item.getGstRate());
            ps.setDouble(6, item.getTaxAmount());
            ps.setDouble(7, item.getLineTotal());
            ps.executeUpdate();
        }
    }

    public Bill getBillById(int billId) throws Exception {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, billId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setBillNo(rs.getString("bill_no"));
                    bill.setCustomerId(rs.getObject("customer_id") == null ? null : rs.getInt("customer_id"));
                    bill.setBillerId(rs.getInt("biller_id"));
                    bill.setSubtotal(rs.getDouble("subtotal"));
                    bill.setDiscount(rs.getDouble("discount"));
                    bill.setTaxTotal(rs.getDouble("tax_total"));
                    bill.setGrandTotal(rs.getDouble("grand_total"));
                    bill.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
                    bill.setCashReceived(rs.getDouble("cash_received"));
                    bill.setChangeAmount(rs.getDouble("change_amount"));
                    bill.setTransactionRef(rs.getString("transaction_ref"));
                    return bill;
                }
            }
        }

        return null;
    }

    public Bill getBillByNo(String billNo) throws Exception {
        String sql = "SELECT * FROM bills WHERE bill_no = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, billNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setBillNo(rs.getString("bill_no"));
                    bill.setCustomerId(rs.getObject("customer_id") == null ? null : rs.getInt("customer_id"));
                    bill.setBillerId(rs.getInt("biller_id"));
                    bill.setSubtotal(rs.getDouble("subtotal"));
                    bill.setDiscount(rs.getDouble("discount"));
                    bill.setTaxTotal(rs.getDouble("tax_total"));
                    bill.setGrandTotal(rs.getDouble("grand_total"));
                    bill.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
                    bill.setCashReceived(rs.getDouble("cash_received"));
                    bill.setChangeAmount(rs.getDouble("change_amount"));
                    bill.setTransactionRef(rs.getString("transaction_ref"));
                    return bill;
                }
            }
        }

        return null;
    }

    public List<BillItem> getBillItems(int billId) throws Exception {
        String sql = "SELECT bi.*, p.product_name FROM bill_items bi JOIN products p ON p.product_id = bi.product_id WHERE bi.bill_id = ? ORDER BY bi.bill_item_id";
        List<BillItem> items = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, billId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BillItem item = new BillItem();
                    item.setBillItemId(rs.getInt("bill_item_id"));
                    item.setBillId(rs.getInt("bill_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setGstRate(rs.getDouble("gst_rate"));
                    item.setTaxAmount(rs.getDouble("tax_amount"));
                    item.setLineTotal(rs.getDouble("line_total"));
                    items.add(item);
                }
            }
        }

        return items;
    }

    public void deductStock(Connection connection, BillItem item) throws Exception {
        String sql = "UPDATE products SET stock_qty = stock_qty - ? WHERE product_id = ? AND active = TRUE AND stock_qty >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, item.getQuantity());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            if (ps.executeUpdate() != 1) {
                throw new IllegalStateException("Insufficient stock for product " + item.getProductName());
            }
        }
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO stock_transactions(product_id,transaction_type,quantity,notes) VALUES(?,'SALE',?,?)")) {
            ps.setInt(1, item.getProductId()); ps.setInt(2, -item.getQuantity()); ps.setString(3, "Bill item"); ps.executeUpdate();
        }
    }

    public List<String[]> getCustomerHistory(String cardId) throws Exception {
        String sql = "SELECT b.bill_no, DATE_FORMAT(b.created_at, '%Y-%m-%d %H:%i:%s') AS created_at, u.full_name, b.grand_total FROM bills b JOIN customers c ON b.customer_id = c.customer_id JOIN app_users u ON b.biller_id = u.user_id WHERE c.card_id = ? ORDER BY b.created_at DESC";
        List<String[]> rows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, cardId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[] {
                        rs.getString("bill_no"),
                        rs.getString("created_at"),
                        rs.getString("full_name"),
                        String.valueOf(rs.getDouble("grand_total"))
                    });
                }
            }
        }

        return rows;
    }

    public List<String[]> getBillerHistory(int billerId) throws Exception {
        String sql = "SELECT b.bill_no, DATE_FORMAT(b.created_at, '%Y-%m-%d %H:%i:%s') AS created_at, COALESCE(c.customer_name, 'WALK-IN') AS customer_name, b.grand_total FROM bills b LEFT JOIN customers c ON b.customer_id = c.customer_id WHERE b.biller_id = ? ORDER BY b.created_at DESC";
        List<String[]> rows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, billerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[] {
                        rs.getString("bill_no"),
                        rs.getString("created_at"),
                        rs.getString("customer_name"),
                        String.valueOf(rs.getDouble("grand_total"))
                    });
                }
            }
        }

        return rows;
    }
}
