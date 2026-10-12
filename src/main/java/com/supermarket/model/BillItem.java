package com.supermarket.model;

public class BillItem {

    private int billItemId;
    private int billId;
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double gstRate;
    private double taxAmount;
    private double lineTotal;

    public BillItem() {
    }

    public BillItem(
            int productId,
            String productName,
            int quantity,
            double unitPrice,
            double gstRate
    ) {

        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.gstRate = gstRate;

        this.lineTotal =
                Math.round(
                        quantity * unitPrice * 100.0
                ) / 100.0;

        this.taxAmount =
                Math.round(
                        this.lineTotal * gstRate / 100.0 * 100.0
                ) / 100.0;
    }

    public int getBillItemId() {
        return billItemId;
    }

    public void setBillItemId(int billItemId) {
        this.billItemId = billItemId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getGstRate() {
        return gstRate;
    }

    public void setGstRate(double gstRate) {
        this.gstRate = gstRate;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }
}