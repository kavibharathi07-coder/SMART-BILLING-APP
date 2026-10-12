package com.supermarket.dao;

import com.supermarket.config.DBConnection;
import com.supermarket.model.Category;
import com.supermarket.model.Product;
import com.supermarket.util.SQLQueryLoader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private String query(String name) {
        return SQLQueryLoader.getQuery(name);
    }

    public Product findByBarcode(String barcode)
            throws SQLException {

        String sql = query("PRODUCT_FIND_BY_BARCODE");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, barcode);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Product findById(int productId)
            throws SQLException {

        String sql = query("PRODUCT_FIND_BY_ID");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Product> getAllProducts()
            throws SQLException {

        String sql = query("PRODUCT_GET_ALL");
        List<Product> products = new ArrayList<>();

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                products.add(map(rs));
            }
        }

        return products;
    }

    public List<Product> search(String term)
            throws SQLException {

        String sql = query("PRODUCT_SEARCH");
        List<Product> products = new ArrayList<>();

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, term);
            ps.setString(2, term);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(map(rs));
                }
            }
        }

        return products;
    }

    public void addProduct(Product p)
            throws SQLException {

        String sql = query("PRODUCT_ADD");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, p.getBarcode());
            ps.setString(2, p.getProductName());
            ps.setInt(3, p.getCategoryId());
            ps.setDouble(4, p.getPrice());
            ps.setDouble(5, p.getGstRate());
            ps.setInt(6, p.getStockQty());
            ps.setInt(7, p.getReorderLevel());

            ps.executeUpdate();
        }
    }

    public void updateProduct(Product p)
            throws SQLException {

        String sql = query("PRODUCT_UPDATE");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setDouble(3, p.getPrice());
            ps.setDouble(4, p.getGstRate());
            ps.setInt(5, p.getReorderLevel());
            ps.setInt(6, p.getProductId());

            ps.executeUpdate();
        }
    }

    public void deactivateProduct(int productId)
            throws SQLException {

        String sql = query("PRODUCT_DEACTIVATE");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public void restockProduct(int productId, int quantity)
            throws SQLException {

        String sql = query("PRODUCT_RESTOCK");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);

            ps.executeUpdate();
        }
    }

    public void addCategory(String categoryName)
            throws SQLException {

        String sql = query("CATEGORY_ADD");

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, categoryName);
            ps.executeUpdate();
        }
    }

    public List<Category> getAllCategories()
            throws SQLException {

        String sql = query("CATEGORY_GET_ALL");
        List<Category> categories = new ArrayList<>();

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                categories.add(
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                    )
                );
            }
        }

        return categories;
    }

    private Product map(ResultSet rs)
            throws SQLException {

        return new Product(
            rs.getInt("product_id"),
            rs.getString("barcode"),
            rs.getString("product_name"),
            rs.getInt("category_id"),
            rs.getString("category_name"),
            rs.getDouble("price"),
            rs.getDouble("gst_rate"),
            rs.getInt("stock_qty"),
            rs.getInt("reorder_level"),
            rs.getBoolean("active")
        );
    }
}
