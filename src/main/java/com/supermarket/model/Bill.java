package com.supermarket.model;

import com.supermarket.enums.PaymentMode;

import java.time.LocalDateTime;

public class Bill {

    private int billId;
    private String billNo;
    private Integer customerId;
    private String customerCardId;
    private String customerName;
    private int billerId;
    private String billerName;
    private double subtotal;
    private double discount;
    private double taxTotal;
    private double grandTotal;
    private PaymentMode paymentMode;
    private double cashReceived;
    private double changeAmount;
    private String transactionRef;
    private LocalDateTime createdAt;

    public Bill() {
    }

    public Bill(
            String billNo,
            Integer customerId,
            int billerId,
            double subtotal,
            double discount,
            double taxTotal,
            double grandTotal,
            PaymentMode paymentMode,
            double cashReceived,
            double changeAmount
    ) {

        this.billNo = billNo;
        this.customerId = customerId;
        this.billerId = billerId;
        this.subtotal = subtotal;
        this.discount = discount;
        this.taxTotal = taxTotal;
        this.grandTotal = grandTotal;
        this.paymentMode = paymentMode;
        this.cashReceived = cashReceived;
        this.changeAmount = changeAmount;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCardId() {
        return customerCardId;
    }

    public void setCustomerCardId(String customerCardId) {
        this.customerCardId = customerCardId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getBillerId() {
        return billerId;
    }

    public void setBillerId(int billerId) {
        this.billerId = billerId;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(double taxTotal) {
        this.taxTotal = taxTotal;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(double cashReceived) {
        this.cashReceived = cashReceived;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
