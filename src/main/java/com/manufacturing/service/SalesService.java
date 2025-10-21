package com.manufacturing.service;

import com.manufacturing.model.Sales;
import com.manufacturing.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;

    public List<Sales> findAll() {
        return salesRepository.findAll();
    }

    public Optional<Sales> findById(String id) {
        return salesRepository.findById(id);
    }

    public Sales save(Sales sales) {
        // Calculate total from all items
        if (sales.getItems() != null && !sales.getItems().isEmpty()) {
            // Ensure each item has calculated line total
            sales.getItems().forEach(item -> {
                if (item.getQuantity() != null && item.getUnitPrice() != null) {
                    item.calculateLineTotal();
                }
            });

            // Calculate grand total
            sales.calculateTotal();
        } else {
            sales.setTotalAmount(0.0);
        }

        return salesRepository.save(sales);
    }

    public void delete(String id) {
        salesRepository.deleteById(id);
    }
}