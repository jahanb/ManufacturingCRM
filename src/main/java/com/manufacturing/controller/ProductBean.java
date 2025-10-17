package com.manufacturing.controller;

import com.manufacturing.model.Product;
import com.manufacturing.service.ProductService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@ViewScoped
public class ProductBean implements Serializable {

    @Autowired
    private ProductService productService;

    private List<Product> products;
    private Product selectedProduct;
    private Product product;

    @PostConstruct
    public void init() {
        loadProducts();
        product = new Product();
    }

    public void loadProducts() {
        products = productService.findAll();
    }

    public void openNew() {
        product = new Product();
        product.setAvailable(true);
        product.setQuantity(0);
        product.setPrice(0.0);
    }

    public void saveProduct() {
        try {
            productService.save(product);
            loadProducts();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Product saved successfully"));
            product = new Product();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save product"));
        }
    }

    public void deleteProduct() {
        try {
            productService.delete(selectedProduct.getId());
            products.remove(selectedProduct);
            selectedProduct = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Product deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete product"));
        }
    }

    // Getters and Setters
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}