package com.manufacturing.service;

import com.manufacturing.model.Product;
import com.manufacturing.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
    public byte[] getImageBytes(String id) {
        return productRepository.findById(id)
                .map(Product::getPhoto)
                .orElseThrow(() -> new RuntimeException("No photo found for product " + id));
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

    public List<Product> findAvailableProducts() {
        return productRepository.findByAvailableTrue();
    }
}