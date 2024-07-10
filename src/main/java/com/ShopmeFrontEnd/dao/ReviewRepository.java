package com.ShopmeFrontEnd.dao;

import com.ShopmeFrontEnd.entity.readonly.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

}
