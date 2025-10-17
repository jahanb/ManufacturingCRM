package com.manufacturing.service;

import com.manufacturing.model.Salesperson;
import com.manufacturing.repository.SalespersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalespersonService {

    private final SalespersonRepository salespersonRepository;

    public SalespersonService(SalespersonRepository salespersonRepository) {
        this.salespersonRepository = salespersonRepository;
    }

    public List<Salesperson> findAll() {
        return salespersonRepository.findAll();
    }

    public Optional<Salesperson> findById(String id) {
        return salespersonRepository.findById(id);
    }

    public Salesperson save(Salesperson salesperson) {
        return salespersonRepository.save(salesperson);
    }

    public void delete(String id) {
        salespersonRepository.deleteById(id);
    }

    public List<Salesperson> findActiveSalespersons() {
        return salespersonRepository.findByActiveTrue();
    }
}