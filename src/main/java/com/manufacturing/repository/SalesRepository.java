package com.manufacturing.repository;

import com.manufacturing.model.Sales;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SalesRepository extends MongoRepository<Sales, String> {
    List<Sales> findByStatus(String status);
    List<Sales> findBySaleDateBetween(Date startDate, Date endDate);
}