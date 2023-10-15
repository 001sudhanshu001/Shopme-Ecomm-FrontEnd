package com.ShopmeFrontEnd.dao;

import com.ShopmeFrontEnd.entity.readonly.CartItem;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepo extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCustomer(Customer customer);
    CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.id = ?2 AND c.product.id = ?3")
    @Modifying
    void updateQuantity(Integer quantity, Integer customerId, Integer productId);

    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
    @Modifying
    void deleteByCustomerAndProduct(Integer customerId, Integer productId);


}
