package com.manufacturing.controller;

import com.manufacturing.model.*;
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
import java.util.ArrayList;
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

    // For adding items to sale
    private SalesItem currentItem;
    private List<SalesItem> tempItems;

    // For adding new customer on the fly
    private Customer newCustomer;

    @PostConstruct
    public void init() {
        loadSales();
        loadDropdowns();
        sales = new Sales();
        tempItems = new ArrayList<>();
        currentItem = new SalesItem();
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
        tempItems = new ArrayList<>();
        currentItem = new SalesItem();
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
            loadDropdowns();
            sales.setCustomer(savedCustomer);

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

    // Item Management
    public void onProductChange() {
        if (currentItem.getProduct() != null) {
            currentItem.setUnitPrice(currentItem.getProduct().getPrice());
            currentItem.calculateLineTotal();
        }
    }

    public void onQuantityChange() {
        currentItem.calculateLineTotal();
    }

    public void onUnitPriceChange() {
        currentItem.calculateLineTotal();
    }

    public void addItem() {
        if (currentItem.getProduct() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning",
                            "Please select a product"));
            return;
        }

        if (currentItem.getQuantity() == null || currentItem.getQuantity() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning",
                            "Please enter quantity"));
            return;
        }

        if (currentItem.getUnitPrice() == null || currentItem.getUnitPrice() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning",
                            "Please enter unit price"));
            return;
        }

        // Calculate line total before adding
        currentItem.calculateLineTotal();

        // Create a copy of the current item to add to the list
        SalesItem itemToAdd = new SalesItem();
        itemToAdd.setProduct(currentItem.getProduct());
        itemToAdd.setQuantity(currentItem.getQuantity());
        itemToAdd.setUnitPrice(currentItem.getUnitPrice());
        itemToAdd.setLineTotal(currentItem.getLineTotal());

        tempItems.add(itemToAdd);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Item added to sale"));

        // Reset for next item
        currentItem = new SalesItem();
    }

    public void removeItem(SalesItem item) {
        tempItems.remove(item);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Item removed"));
    }

    public Double calculateGrandTotal() {
        if (tempItems == null || tempItems.isEmpty()) {
            return 0.0;
        }
        return tempItems.stream()
                .mapToDouble(item -> item.getLineTotal() != null ? item.getLineTotal() : 0.0)
                .sum();
    }

    public void saveSales() {
        try {
            if (sales.getSalesperson() == null || sales.getCustomer() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning",
                                "Please select salesperson and customer"));
                return;
            }

            if (tempItems == null || tempItems.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning",
                                "Please add at least one item to the sale"));
                return;
            }

            // Set items and calculate total
            sales.setItems(new ArrayList<>(tempItems));
            sales.calculateTotal();

            salesService.save(sales);
            loadSales();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Sales saved successfully with " + tempItems.size() + " items"));

            // Reset everything
            sales = new Sales();
            tempItems = new ArrayList<>();
            currentItem = new SalesItem();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to save sales: " + e.getMessage()));
            e.printStackTrace();
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
                            "Failed to delete sales: " + e.getMessage()));
        }
    }

    public void editSales() {
        if (selectedSales != null) {
            sales = selectedSales;
            tempItems = new ArrayList<>(sales.getItems() != null ? sales.getItems() : new ArrayList<>());
            currentItem = new SalesItem();
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

    public SalesItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(SalesItem currentItem) {
        this.currentItem = currentItem;
    }

    public List<SalesItem> getTempItems() {
        return tempItems;
    }

    public void setTempItems(List<SalesItem> tempItems) {
        this.tempItems = tempItems;
    }
}