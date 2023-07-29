package com.ShopmeFrontEnd.dao;


import com.ShopmeFrontEnd.entity.readonly.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepoFrontEnd extends JpaRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();

    @Query("SELECT c FROM Country c WHERE c.code = ?1")
    Country findByCode(String code);
}
