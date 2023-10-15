package com.ShopmeFrontEnd.dao;

import com.ShopmeFrontEnd.entity.readonly.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoryRepoFrontEnd extends JpaRepository<Category, Integer> {

    // We will show only those categories which are enabled
    @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC")
    List<Category> findAllEnabled();

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
    Category findByAliasEnabled(String alias);
}
