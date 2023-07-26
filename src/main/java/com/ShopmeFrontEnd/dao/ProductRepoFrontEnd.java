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

    // Since it is Mysql Query so we used table name and column names, instead of Entity and field names
     @Query(value = "SELECT * FROM products WHERE enabled = true AND "
             + "MATCH(name, short_description, full_description) AGAINST (?1)",
             nativeQuery = true)
    Page<Product> search(String keyword, Pageable pageable);
}

