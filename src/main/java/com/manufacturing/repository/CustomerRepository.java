package com.manufacturing.repository;

import com.manufacturing.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    List<Customer> findByActiveTrue();
    List<Customer> findByCustomerType(String customerType);
    List<Customer> findByNameContainingIgnoreCase(String name);
}