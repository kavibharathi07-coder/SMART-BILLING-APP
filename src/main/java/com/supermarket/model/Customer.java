package com.supermarket.model;

public class Customer {

    private int customerId;
    private String cardId;
    private String customerName;
    private String phone;
    private String email;

    public Customer() {
    }

    public Customer(
            int customerId,
            String cardId,
            String customerName,
            String phone,
            String email) {

        this.customerId = customerId;
        this.cardId = cardId;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("ID: %-5d Card: %-14s Name: %-22s Phone: %-15s Email: %s",
                customerId, cardId, customerName, phone == null ? "-" : phone, email == null ? "-" : email);
    }
}
