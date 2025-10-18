package com.manufacturing.service;

import com.manufacturing.model.Customer;
import com.manufacturing.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(String id) {
        return customerRepository.findById(id);
    }

    public Customer save(Customer customer) {
        if (customer.getCreatedDate() == null) {
            customer.setCreatedDate(LocalDateTime.now());
        }
        return customerRepository.save(customer);
    }

    public void delete(String id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> findActiveCustomers() {
        return customerRepository.findByActiveTrue();
    }

    public List<Customer> searchByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }
}