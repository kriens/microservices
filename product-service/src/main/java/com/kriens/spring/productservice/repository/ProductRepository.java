package com.kriens.spring.productservice.repository;

import com.kriens.spring.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
