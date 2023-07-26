package com.ShopmeFrontEnd.dao;


import com.ShopmeFrontEnd.entity.readonly.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepoFrontEnd extends JpaRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();
}
