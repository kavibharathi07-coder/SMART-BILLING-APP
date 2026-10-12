package com.supermarket.ui;

import com.supermarket.model.Category;
import com.supermarket.model.Product;
import com.supermarket.service.ProductService;
import com.supermarket.util.ConsoleUtil;
import com.supermarket.util.InputUtil;

public class ProductMenu {

    private final ProductService service;
    
    public ProductMenu(ProductService service) {
        this.service = service;
    }

    public void show() {

        while (true) {

            ConsoleUtil.panelTop("PRODUCT MANAGEMENT");
            ConsoleUtil.panelMenu(1, "Add Product");
            ConsoleUtil.panelMenu(2, "View Products");
            ConsoleUtil.panelMenu(3, "Search Product");
            ConsoleUtil.panelMenu(4, "Update Product");
            ConsoleUtil.panelMenu(5, "Deactivate Product");
            ConsoleUtil.panelMenu(6, "Restock Product");
            ConsoleUtil.panelMenu(7, "View Categories");
            ConsoleUtil.panelMenu(8, "Back to Admin Dashboard");
            ConsoleUtil.panelBottom();

            String choice = InputUtil.readRequired(
                    "Enter your choice: "
            );

            try {

                switch (choice) {

                    case "1":
                        addProduct();
                        break;

                    case "2":
                        viewProducts();
                        break;

                    case "3":
                        search();
                        break;

                    case "4":
                        update();
                        break;

                    case "5":
                        deactivate();
                        break;

                    case "6":
                        restock();
                        break;

                    case "7":
                        viewCategories();
                        break;

                    case "8":
                        System.out.println(
                                "Returning to Admin Dashboard..."
                        );
                        return;

                    default:
                        System.out.println(
                                "Invalid choice! Please select 1-8."
                        );
                }

            } catch (Exception e) {

                ConsoleUtil.error("Operation failed: " + e.getMessage());
            }

            ConsoleUtil.pause();
        }
    }

    private void addProduct() throws Exception {

        ConsoleUtil.header("ADD NEW PRODUCT");

        String barcode = InputUtil.readRequired("Barcode: ");
        String name = InputUtil.readRequired("Product name: ");

        viewCategories();

        int categoryId = InputUtil.readPositiveInt("Category ID: ");
        double price = InputUtil.readNonNegativeDouble("Price: ");
        double gst = InputUtil.readNonNegativeDouble("GST rate (%): ");
        int stock = InputUtil.readInt("Opening stock: ");
        int reorder = InputUtil.readInt("Reorder level: ");

        service.addProduct(
                new Product(
                        0,
                        barcode,
                        name,
                        categoryId,
                        null,
                        price,
                        gst,
                        stock,
                        reorder,
                        true
                )
        );

        ConsoleUtil.success("Product added successfully.");
    }

    private void viewProducts() throws Exception {

        ConsoleUtil.header("ALL PRODUCTS");

        System.out.printf(
                "%-5s %-8s %-22s %-16s %-11s %-7s %-6s %-8s%n",
                "ID",
                "BARCODE",
                "PRODUCT",
                "CATEGORY",
                "PRICE",
                "GST",
                "STOCK",
                "STATUS"
        );

        ConsoleUtil.separator();

        for (Product p : service.getAllProducts()) {
            System.out.printf("%-5d %s%n", p.getProductId(), p);
        }
    }

    private void search() throws Exception {

        ConsoleUtil.header("SEARCH PRODUCT");

        String text = InputUtil.readRequired("Search text: ");

        for (Product p : service.search(text)) {
            System.out.println(p);
        }
    }

    private void update() throws Exception {

        ConsoleUtil.header("UPDATE PRODUCT");

        viewProducts();
        int id = InputUtil.readPositiveInt("Product ID from the list above: ");

        Product existing = service.findById(id);

        if (existing == null) {
            ConsoleUtil.error("Product not found.");
            return;
        }

        existing.setProductName(
                InputUtil.readRequired("Product name: ")
        );

        viewCategories();

        existing.setCategoryId(
                InputUtil.readPositiveInt("Category ID: ")
        );

        existing.setPrice(
                InputUtil.readNonNegativeDouble("Price: ")
        );

        existing.setGstRate(
                InputUtil.readNonNegativeDouble("GST rate (%): ")
        );

        existing.setReorderLevel(
                InputUtil.readInt("Reorder level: ")
        );

        service.updateProduct(existing);

        ConsoleUtil.success("Product updated successfully.");
    }

    private void deactivate() throws Exception {

        ConsoleUtil.header("DEACTIVATE PRODUCT");

        viewProducts();
        int id = InputUtil.readPositiveInt("Product ID to deactivate: ");

        service.deactivate(id);

        ConsoleUtil.success("Product deactivated successfully.");
    }

    private void restock() throws Exception {

        ConsoleUtil.header("RESTOCK PRODUCT");

        viewProducts();
        int id = InputUtil.readPositiveInt("Product ID to restock: ");
        int qty = InputUtil.readPositiveInt("Restock quantity: ");

        service.restock(id, qty);

        ConsoleUtil.success("Stock updated successfully.");
    }

    private void viewCategories() throws Exception {

        ConsoleUtil.header("AVAILABLE CATEGORIES");

        for (Category c : service.getCategories()) {
            System.out.println(c);
        }

        ConsoleUtil.separator();
    }
}
