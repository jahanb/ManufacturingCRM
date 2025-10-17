package com.manufacturing.repository;

import com.manufacturing.model.Salesperson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalespersonRepository extends MongoRepository<Salesperson, String> {
    List<Salesperson> findByActiveTrue();
}