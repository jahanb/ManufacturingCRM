package com.manufacturing.controller;

import com.manufacturing.model.Customer;
import com.manufacturing.service.CustomerService;
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
public class CustomerBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private CustomerService customerService;

    private List<Customer> customers;
    private Customer selectedCustomer;
    private Customer customer;

    @PostConstruct
    public void init() {
        loadCustomers();
        customer = new Customer();
    }

    public void loadCustomers() {
        customers = customerService.findAll();
    }

    public void openNew() {
        customer = new Customer();
        customer.setActive(true);
        customer.setCustomerType("REGULAR");
        customer.setCreatedDate(LocalDateTime.now());
    }

    public void saveCustomer() {
        try {
            customerService.save(customer);
            loadCustomers();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Customer saved successfully"));
            customer = new Customer();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to save customer: " + e.getMessage()));
        }
    }

    public void deleteCustomer() {
        try {
            customerService.delete(selectedCustomer.getId());
            customers.remove(selectedCustomer);
            selectedCustomer = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Customer deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to delete customer"));
        }
    }

    // Getters and Setters
    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}