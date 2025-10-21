package com.manufacturing.controller;

import com.manufacturing.model.Product;
import com.manufacturing.service.ProductService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Named("productBean")
@ViewScoped
@Component
public class ProductBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private ProductService productService;

    private Product product;
    private List<Product> products;
    private UploadedFile uploadedFile; // Add this field

    @PostConstruct
    public void init() {
        System.out.println("ProductBean initialized");
        loadProducts();
        product = new Product(); // Initialize with empty product
    }

    public void loadProducts() {
        products = productService.findAll();
        System.out.println("Loaded " + products.size() + " products");
    }

    public void openNew() {
        product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setAvailable(true);
        product.setStatus("INSTOCK");
        uploadedFile = null; // Clear any previous upload
        System.out.println("Opening new product with ID: " + product.getId());
    }

    public void editProduct(Product selectedProduct) {
        // Create a copy to avoid direct reference issues
        this.product = selectedProduct;
        uploadedFile = null; // Clear any previous upload
        System.out.println("Editing product: " + product.getId() + ", has photo: " + (product.getPhoto() != null));
    }

    public void deleteProduct(Product productToDelete) {
        try {
            productService.delete(productToDelete);
            products.remove(productToDelete);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Success", "Product deleted successfully"));
            loadProducts();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Failed to delete product: " + e.getMessage()));
        }
    }

    public void saveProduct() {
        try {
            System.out.println("=== Saving Product ===");
            System.out.println("Product code: " + product.getCode());

            // Process uploaded file BEFORE saving if it exists
            if (uploadedFile != null && uploadedFile.getContent() != null) {
                System.out.println("Processing uploaded file: " + uploadedFile.getFileName());
                handleUploadedFile();
            }

            // Generate ID if new product
            if (product.getId() == null || product.getId().isEmpty()) {
                product.setId(UUID.randomUUID().toString());
                System.out.println("Generated new ID: " + product.getId());
            }

            System.out.println("Has photo: " + (product.getPhoto() != null));
            if (product.getPhoto() != null) {
                System.out.println("Photo size: " + product.getPhoto().length + " bytes");
            }

            // Save product (works for both create and update)
            Product savedProduct = productService.save(product);
            System.out.println("Product saved successfully with ID: " + savedProduct.getId());

            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Product saved successfully");

            loadProducts(); // Reload the list
            product = new Product(); // Reset to empty product
            uploadedFile = null; // Clear upload

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save product: " + e.getMessage());
        }
    }

    private void handleUploadedFile() {
        try {
            if (uploadedFile == null || uploadedFile.getContent() == null) {
                return;
            }

            // Validate file size (5MB limit)
            if (uploadedFile.getSize() > 5_000_000) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "File size must be less than 5MB");
                return;
            }

            // Validate file type
            String contentType = uploadedFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Only image files are allowed");
                return;
            }

            // Set photo data to product
            product.setPhoto(uploadedFile.getContent());
            product.setPhotoContentType(contentType);
            product.setPhotoFilename(uploadedFile.getFileName());

            System.out.println("Photo attached: " + uploadedFile.getFileName() +
                    ", size: " + uploadedFile.getSize() + " bytes");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR processing uploaded file: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to process photo: " + e.getMessage());
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        // This method is no longer used with simple mode, but keep it for compatibility
        System.out.println("handleFileUpload called (should not happen with simple mode)");
    }

    public void removePhoto() {
        if (product != null) {
            product.setPhoto(null);
            product.setPhotoContentType(null);
            product.setPhotoFilename(null);
            uploadedFile = null;

            System.out.println("Photo removed from product");
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Photo removed");
        }
    }

    // Getters and Setters
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
        System.out.println("File selected: " + (uploadedFile != null ? uploadedFile.getFileName() : "null"));
    }

    public String getProductImageAsBase64(Product prod) {
        if (prod != null && prod.getPhoto() != null && prod.getPhoto().length > 0) {
            try {
                String base64Image = java.util.Base64.getEncoder().encodeToString(prod.getPhoto());
                return "data:" + prod.getPhotoContentType() + ";base64," + base64Image;
            } catch (Exception e) {
                System.err.println("Error encoding image to base64: " + e.getMessage());
                e.printStackTrace();
            }
        }
        // Return 1x1 transparent PNG
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
    }

    public boolean hasPhoto() {
        boolean result = product != null && product.getPhoto() != null && product.getPhoto().length > 0;
        System.out.println("hasPhoto() called: " + result);
        return result;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public Product getProduct() {
        if (product == null) {
            System.out.println("WARNING: getProduct() called but product is NULL, creating new one");
            product = new Product();
        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
