package com.manufacturing.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "sales")
public class Sales {
    @Id
    private String id;

    @DBRef
    private Salesperson salesperson;

    @DBRef
    private Customer customer;

    // Multiple items in one sale
    private List<SalesItem> items;

    private Double totalAmount; // Sum of all line totals
    private LocalDateTime saleDate;
    private String status; // PENDING, COMPLETED, CANCELLED
    private String notes;

    // Constructors
    public Sales() {
        this.items = new ArrayList<>();
    }

    public Sales(String id, Salesperson salesperson, Customer customer, List<SalesItem> items,
                 Double totalAmount, LocalDateTime saleDate, String status, String notes) {
        this.id = id;
        this.salesperson = salesperson;
        this.customer = customer;
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
        this.status = status;
        this.notes = notes;
    }

    // Calculate total from all items
    public void calculateTotal() {
        if (items != null && !items.isEmpty()) {
            totalAmount = items.stream()
                    .mapToDouble(item -> item.getLineTotal() != null ? item.getLineTotal() : 0.0)
                    .sum();
        } else {
            totalAmount = 0.0;
        }
    }

    // Add item to sale
    public void addItem(SalesItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        calculateTotal();
    }

    // Remove item from sale
    public void removeItem(SalesItem item) {
        if (items != null) {
            items.remove(item);
            calculateTotal();
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Salesperson getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(Salesperson salesperson) {
        this.salesperson = salesperson;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<SalesItem> getItems() {
        return items;
    }

    public void setItems(List<SalesItem> items) {
        this.items = items;
        calculateTotal();
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}