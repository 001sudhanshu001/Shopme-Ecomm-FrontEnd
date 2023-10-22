package com.ShopmeFrontEnd.dao;

import com.ShopmeFrontEnd.entity.readonly.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Integer> {
}
