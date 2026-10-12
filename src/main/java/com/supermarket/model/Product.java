package com.supermarket.model;

public class Product {

    private int productId;
    private String barcode;
    private String productName;
    private int categoryId;
    private String categoryName;
    private double price;
    private double gstRate;
    private int stockQty;
    private int reorderLevel;
    private boolean active;

    public Product() {
    }

    public Product(
            int productId,
            String barcode,
            String productName,
            int categoryId,
            String categoryName,
            double price,
            double gstRate,
            int stockQty,
            int reorderLevel,
            boolean active
    ) {

        this.productId = productId;
        this.barcode = barcode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.gstRate = gstRate;
        this.stockQty = stockQty;
        this.reorderLevel = reorderLevel;
        this.active = active;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getGstRate() {
        return gstRate;
    }

    public void setGstRate(double gstRate) {
        this.gstRate = gstRate;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {

        return String.format(
                "%-8s %-22s %-16s ₹%-9.2f %-6.2f%% %-6d %-8s",
                barcode,
                productName,
                categoryName == null ? "-" : categoryName,
                price,
                gstRate,
                stockQty,
                active ? "ACTIVE" : "OFF"
        );
    }
}