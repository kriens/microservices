package com.kriens.spring.orderservice.repository;

import com.kriens.spring.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
