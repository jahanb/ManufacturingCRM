package com.manufacturing.service;

import com.manufacturing.model.Sales;
import com.manufacturing.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    public List<Sales> findAll() {
        return salesRepository.findAll();
    }

    public Optional<Sales> findById(String id) {
        return salesRepository.findById(id);
    }

    public Sales save(Sales sales) {
        // Calculate total amount
        if (sales.getQuantity() != null && sales.getUnitPrice() != null) {
            sales.setTotalAmount(sales.getQuantity() * sales.getUnitPrice());
        }
        return salesRepository.save(sales);
    }

    public void delete(String id) {
        salesRepository.deleteById(id);
    }
}