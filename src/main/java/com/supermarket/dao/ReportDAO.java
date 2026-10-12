package com.supermarket.dao;

import com.supermarket.config.DBConnection;
import com.supermarket.util.SQLQueryLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    private String query(String name) {
        return SQLQueryLoader.getQuery(name);
    }

    public Map<String, Object> getDailySales(LocalDate date) throws Exception {
        String sql = query("REPORT_DAILY_SALES");
        Map<String, Object> result = new HashMap<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.put("total_bills", rs.getInt("total_bills"));
                    result.put("subtotal", rs.getDouble("subtotal"));
                    result.put("discount", rs.getDouble("discount"));
                    result.put("tax", rs.getDouble("tax"));
                    result.put("total_sales", rs.getDouble("total_sales"));
                    result.put("cash_collected", rs.getDouble("cash_collected"));
                }
            }
        }

        return result;
    }

    public List<Map<String, Object>> getBillerSales() throws Exception {
        String sql = query("REPORT_BILLER_SALES");
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("biller", rs.getString("biller"));
                row.put("bills", rs.getInt("bills"));
                row.put("sales", rs.getDouble("sales"));
                rows.add(row);
            }
        }

        return rows;
    }

    public List<Map<String, Object>> getLowStock(int limit) throws Exception {
        String sql = query("REPORT_LOW_STOCK");
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("product_id", rs.getInt("product_id"));
                    row.put("product_name", rs.getString("product_name"));
                    row.put("stock_qty", rs.getInt("stock_qty"));
                    row.put("reorder_level", rs.getInt("reorder_level"));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    public List<Map<String, Object>> getAuditLogs() throws Exception {
        String sql = query("REPORT_AUDIT_LOGS");
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("audit_id", rs.getInt("audit_id"));
                row.put("user_id", rs.getObject("user_id"));
                row.put("action_type", rs.getString("action_type"));
                row.put("description", rs.getString("description"));
                row.put("created_at", rs.getTimestamp("created_at"));
                rows.add(row);
            }
        }

        return rows;
    }
}
