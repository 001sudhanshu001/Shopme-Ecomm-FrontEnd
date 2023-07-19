package com.ShopmeFrontEnd.dao;

import com.ShopmeFrontEnd.entity.readonly.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepoFrontEnd extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true "
            + "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
            + " ORDER BY p.name ASC")
    Page<Product> listProductByCategory(Integer categoryId, String categoryMatch, Pageable pageable);

    Product findByAlias(String alias);
}

