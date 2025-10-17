package com.manufacturing.controller;

import com.manufacturing.model.Product;
import com.manufacturing.model.Sales;
import com.manufacturing.model.Salesperson;
import com.manufacturing.service.ProductService;
import com.manufacturing.service.SalesService;
import com.manufacturing.service.SalespersonService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ViewScoped
public class SalesBean implements Serializable {

    @Autowired
    private SalesService salesService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SalespersonService salespersonService;

    private List<Sales> salesList;
    private Sales selectedSales;
    private Sales sales;

    private List<Product> availableProducts;
    private List<Salesperson> activeSalespersons;

    @PostConstruct
    public void init() {
        loadSales();
        loadDropdowns();
        sales = new Sales();
    }

    public void loadSales() {
        salesList = salesService.findAll();
    }

    public void loadDropdowns() {
        availableProducts = productService.findAvailableProducts();
        activeSalespersons = salespersonService.findActiveSalespersons();
    }

    public void openNew() {
        sales = new Sales();
        sales.setSaleDate(LocalDateTime.now());
        sales.setStatus("PENDING");
    }

    public void onProductChange() {
        if (sales.getProduct() != null) {
            sales.setUnitPrice(sales.getProduct().getPrice());
            calculateTotal();
        }
    }

    public void onQuantityChange() {
        calculateTotal();
    }

    public void calculateTotal() {
        if (sales.getQuantity() != null && sales.getUnitPrice() != null) {
            sales.setTotalAmount(sales.getQuantity() * sales.getUnitPrice());
        }
    }

    public void saveSales() {
        try {
            if (sales.getSalesperson() == null || sales.getProduct() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Please select both product and salesperson"));
                return;
            }

            salesService.save(sales);
            loadSales();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Sales saved successfully"));
            sales = new Sales();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save sales: " + e.getMessage()));
        }
    }

    public void deleteSales() {
        try {
            salesService.delete(selectedSales.getId());
            salesList.remove(selectedSales);
            selectedSales = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Sales deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete sales"));
        }
    }

    // Getters and Setters
    public List<Sales> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<Sales> salesList) {
        this.salesList = salesList;
    }

    public Sales getSelectedSales() {
        return selectedSales;
    }

    public void setSelectedSales(Sales selectedSales) {
        this.selectedSales = selectedSales;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    public List<Product> getAvailableProducts() {
        return availableProducts;
    }

    public void setAvailableProducts(List<Product> availableProducts) {
        this.availableProducts = availableProducts;
    }

    public List<Salesperson> getActiveSalespersons() {
        return activeSalespersons;
    }

    public void setActiveSalespersons(List<Salesperson> activeSalespersons) {
        this.activeSalespersons = activeSalespersons;
    }
}