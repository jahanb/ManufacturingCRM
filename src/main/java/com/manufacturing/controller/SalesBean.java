package com.manufacturing.controller;

import com.manufacturing.model.Customer;
import com.manufacturing.model.Product;
import com.manufacturing.model.Sales;
import com.manufacturing.model.Salesperson;
import com.manufacturing.service.CustomerService;
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

    private static final long serialVersionUID = 1L;

    @Autowired
    private SalesService salesService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SalespersonService salespersonService;

    @Autowired
    private CustomerService customerService;

    private List<Sales> salesList;
    private Sales selectedSales;
    private Sales sales;

    private List<Product> availableProducts;
    private List<Salesperson> activeSalespersons;
    private List<Customer> activeCustomers;

    // For adding new customer on the fly
    private Customer newCustomer;

    @PostConstruct
    public void init() {
        loadSales();
        loadDropdowns();
        sales = new Sales();
        resetNewCustomer();
    }

    public void loadSales() {
        salesList = salesService.findAll();
    }

    public void loadDropdowns() {
        availableProducts = productService.findAvailableProducts();
        activeSalespersons = salespersonService.findActiveSalespersons();
        activeCustomers = customerService.findActiveCustomers();
    }

    public void openNew() {
        sales = new Sales();
        sales.setSaleDate(LocalDateTime.now());
        sales.setStatus("PENDING");
        resetNewCustomer();
    }

    public void openNewCustomerDialog() {
        resetNewCustomer();
    }

    private void resetNewCustomer() {
        newCustomer = new Customer();
        newCustomer.setActive(true);
        newCustomer.setCustomerType("REGULAR");
        newCustomer.setCreatedDate(LocalDateTime.now());
    }

    public void saveNewCustomer() {
        try {
            if (newCustomer.getName() == null || newCustomer.getName().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning",
                                "Customer name is required"));
                return;
            }

            Customer savedCustomer = customerService.save(newCustomer);
            loadDropdowns(); // Reload customers list
            sales.setCustomer(savedCustomer); // Set the newly created customer

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Customer created and selected"));

            resetNewCustomer();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to create customer: " + e.getMessage()));
        }
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
            if (sales.getSalesperson() == null || sales.getProduct() == null || sales.getCustomer() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning",
                                "Please select salesperson, product, and customer"));
                return;
            }

            salesService.save(sales);
            loadSales();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Sales saved successfully"));
            sales = new Sales();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to save sales: " + e.getMessage()));
        }
    }

    public void deleteSales() {
        try {
            salesService.delete(selectedSales.getId());
            salesList.remove(selectedSales);
            selectedSales = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Sales deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to delete sales"));
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

    public List<Customer> getActiveCustomers() {
        return activeCustomers;
    }

    public void setActiveCustomers(List<Customer> activeCustomers) {
        this.activeCustomers = activeCustomers;
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }
}