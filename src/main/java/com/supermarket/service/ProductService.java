package com.supermarket.service;

import com.supermarket.dao.ProductDAO;
import com.supermarket.model.Category;
import com.supermarket.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductDAO dao =
            new ProductDAO();

    public void addProduct(
            Product product
    ) throws SQLException {

        validate(product);
        dao.addProduct(product);
    }

    public void updateProduct(
            Product product
    ) throws SQLException {

        validate(product);
        dao.updateProduct(product);
    }

    public Product searchProduct(
            String barcode
    ) throws SQLException {

        return dao.findByBarcode(barcode);
    }

    public Product findById(
            int id
    ) throws SQLException {

        return dao.findById(id);
    }

    public List<Product> getAllProducts()
            throws SQLException {

        return dao.getAllProducts();
    }

    public List<Product> search(
            String text
    ) throws SQLException {

        return dao.search(text);
    }

    public void deactivate(
            int productId
    ) throws SQLException {

        dao.deactivateProduct(productId);
    }

    public void restock(
            int productId,
            int quantity
    ) throws SQLException {

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Restock quantity must be > 0"
            );
        }

        dao.restockProduct(
                productId,
                quantity
        );
    }

    public void addCategory(
            String name
    ) throws SQLException {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Category name cannot be empty"
            );
        }

        dao.addCategory(name.trim());
    }

    public List<Category> getCategories()
            throws SQLException {

        return dao.getAllCategories();
    }

    private void validate(Product p) {

        if (p.getBarcode() == null
                || p.getBarcode().isBlank()) {

            throw new IllegalArgumentException(
                    "Barcode cannot be empty"
            );
        }

        if (p.getProductName() == null
                || p.getProductName().isBlank()) {

            throw new IllegalArgumentException(
                    "Product name cannot be empty"
            );
        }

        if (p.getPrice() < 0) {

            throw new IllegalArgumentException(
                    "Price cannot be negative"
            );
        }

        if (p.getGstRate() < 0) {

            throw new IllegalArgumentException(
                    "GST rate cannot be negative"
            );
        }

        if (p.getStockQty() < 0) {

            throw new IllegalArgumentException(
                    "Stock cannot be negative"
            );
        }

        if (p.getReorderLevel() < 0) {

            throw new IllegalArgumentException(
                    "Reorder level cannot be negative"
            );
        }
    }
}