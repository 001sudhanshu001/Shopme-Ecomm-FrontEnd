package com.ShopmeFrontEnd.dao;

import com.ShopmeFrontEnd.entity.readonly.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
    Page<Review> findByCustomer(Integer customerId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND ("
            + "r.headline LIKE %?2% OR r.comment LIKE %?2% OR "
            + "r.product.name LIKE %?2%)")
    Page<Review> findByCustomer(Integer customerId, String keyword, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.id = ?2")
    Review findByCustomerAndId(Integer customerId, Integer reviewId);
}
